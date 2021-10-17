package org.sagebionetworks.research.sagearc

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.healthymedium.arc.api.RestClient
import com.healthymedium.arc.api.RestResponse
import com.healthymedium.arc.api.models.*
import com.healthymedium.arc.core.Application
import com.healthymedium.arc.study.Study
import com.healthymedium.arc.study.TestSession
import com.healthymedium.arc.utilities.CacheManager
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.swagger.annotations.Api
import org.joda.time.DateTimeZone
import org.sagebionetworks.bridge.android.manager.AuthenticationManager
import org.sagebionetworks.bridge.android.manager.ParticipantRecordManager
import org.sagebionetworks.bridge.android.manager.UploadManager
import org.sagebionetworks.bridge.rest.model.Message
import org.sagebionetworks.bridge.rest.model.ReportData
import org.sagebionetworks.bridge.rest.model.ReportDataList
import org.sagebionetworks.bridge.rest.model.UserSessionInfo
import org.sagebionetworks.research.domain.Schema
import org.sagebionetworks.research.domain.result.AnswerResultType
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase
import org.sagebionetworks.research.domain.result.implementations.FileResultBase
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase
import org.sagebionetworks.research.sageresearch.extensions.toJodaLocalDate
import org.sagebionetworks.research.sageresearch.extensions.toThreeTenInstant
import org.sagebionetworks.research.sageresearch.extensions.toThreeTenLocalDate
import org.sagebionetworks.research.sageresearch_app_sdk.TaskResultUploader
import org.threeten.bp.Instant
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.io.File
import java.util.*

class SageRestApi(val reportManager: ParticipantRecordManager,
                  val authManager: AuthenticationManager,
                  val taskResultUploader: TaskResultUploader,
                  val uploadManager: UploadManager):
        RestClient<Api>(null), ReportUploadListener {

    companion object {
        var LOG_TAG = SageRestApi::class.java.simpleName

        val SIGNATURE_TASK_IDENTIFIER = "Signature"
        val TEST_SESSION_TASK_IDENTIFIER = "TestSession"
        val STUDY_PERIOD_SCHEDULE_TASK_IDENTIFIER = "StudyPeriodSchedule"
        val WAKE_SLEEP_TASK_IDENTIFIER = "WakeSleep"

        const val JSON_MIME_CONTENT_TYPE = "application/json"
        const val PNG_MIME_CONTENT_TYPE = "image/png"

        const val DATA_FILE_NAME = "data"
        const val JSON_UPLOAD_FILE_NAME = "$DATA_FILE_NAME.json"
        const val PNG_UPLOAD_FILE_NAME = "$DATA_FILE_NAME.png"

        public const val MigratedIdentifier = "Migrated"
        public const val AvailabilityIdentifier = "Availability"
        public const val TestScheduleIdentifier = "TestSchedule"
        public const val CompletedTestsIdentifier = "CompletedTests"

        /**
         * @property reportSingletonDate used when doing read/write on singleton category reports
         */
        val reportSingletonDate: Instant = Instant.EPOCH
        val reportSingletonJodaLocalDate = org.joda.time.LocalDate(reportSingletonDate.toEpochMilli(), DateTimeZone.UTC)
        val reportSingletonLocalDate = reportSingletonJodaLocalDate.toThreeTenLocalDate()
    }

    // Used to upload to Sage Bridge server
    private val compositeDisposable = CompositeDisposable()
    private val compositeSubscription = CompositeSubscription()

    private val earningsController = SageEarningsController()
    private val completedTestManager = CompletedTestsSingletonReport(this)

    fun createFailureRestResponse(error: Throwable): RestResponse {
        val response = RestResponse()
        response.code = 401
        response.successful = false
        val errorJson = JsonObject()
        errorJson.addProperty("error", error.localizedMessage)
        response.errors = errorJson
        return response
    }

    fun createSuccessRestResponse(): RestResponse {
        val response = RestResponse()
        response.code = 200
        response.successful = true
        return response
    }

    init {
        earningsController.completedTests = completedTestManager.current.completed
    }

    // public calls --------------------------------------------------------------------------------
    override fun registerDevice(registration: DeviceRegistration?, listener: Listener?) {
        // No-op needed for HASD, it uses the registerDevice function below
    }

    override fun registerDevice(participantId: String?,
                                authorizationCode: String?,
                                existingUser: Boolean,
                                listener: Listener?) {

        val externalId = participantId ?: ""
        val password = "$authorizationCode"

        compositeSubscription.add(
                authManager.signInWithExternalId(externalId, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ userSessionInfo: UserSessionInfo? ->
                            afterSuccessfulSignIn(listener)
                        }) { t: Throwable ->
                            Log.e(LOG_TAG, "Error signing in ${t.localizedMessage}")
                            listener?.onFailure(createFailureRestResponse(t))
                        })
    }

    private fun afterSuccessfulSignIn(listener: Listener?) {
        loadHistoryFromBridge { _ /* data */, error ->
            error?.let {
                Log.e(LOG_TAG, "Error signing in $it")
                listener?.onFailure(createFailureRestResponse(Throwable(it)))
                return@loadHistoryFromBridge
            }
            // TODO: mdephillips 9/20/21 after we validate ALL users have migrated to bridge,
            // TODO: mdephillips 9/20/21 we are safe to remove this call in an app update
            val json = gson.toJson(MigratedStatus(true))
            uploadReport(MigratedIdentifier, json, listener)
        }
    }

    override fun requestVerificationCode(participantId: String?, listener: Listener?) {
        val i = 0
//        if (Config.REST_BLACKHOLE) {
//            return
//        }
//        Log.i("RestClient", "requestVerificationCode")
//        val request = VerificationCodeRequest()
//        request.arcId = participantId
//        val json = serialize(request)
//        val call = service.requestVerificationCode(json)
//        call.enqueue(createCallback(listener))
    }

    override fun requestAuthDetails(participantId: String?, listener: Listener?) {
        val i = 0
//        if (Config.REST_BLACKHOLE) {
//            return
//        }
//        Log.i("RestClient", "requestAuthDetails")
//        val request = AuthDetailsRequest()
//        request.arcId = participantId
//        val json = serialize(request)
//        val call = service.requestAuthDetails(json)
//        call.enqueue(createCallback(listener))
    }

    /**
     * Subscribes to the completable using the CompositeDisposable
     * This is open for unit testing purposes to to run a blockingGet() instead of an asynchronous subscribe
     */
    private fun uploadCompletableAsync(
            completable: Completable, successMsg: String, errorMsg: String,
            uploadFailedFilesOnSuccess: Boolean = true) {

        compositeDisposable.add(completable
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i(LOG_TAG, successMsg)
                    // If we succeeded with the upload, always try to re-upload
                    // any failed uploads at this time unless specifying not to
                    if (uploadFailedFilesOnSuccess) {
                        uploadCompletableAsync(
                                RxJavaInterop.toV2Completable(uploadManager.processUploadFiles()),
                                "Successfully uploaded all past data in upload manager",
                                "Failed to uploaded all past data in upload manager",
                                false) // avoid infinite upload loop
                    }
                }, {
                    Log.w(LOG_TAG, "$errorMsg ${it.localizedMessage}")
                }))
    }

    override fun getStudySummary(listener: Listener) {
        Log.v(LOG_TAG, "getStudySummary")
        val summary = earningsController.getCurrentStudySummary()
        val summaryTree = gson.toJsonTree(summary)
        val response = createResponse("summary", summaryTree)
        listener.onSuccess(response)
    }

    override fun getStudyProgress(listener: Listener) {
        Log.v(LOG_TAG, "getStudyProgress")
        super.getStudyProgress(listener)
    }

    override fun getCycleProgress(index: Int, listener: Listener) {
        Log.v(LOG_TAG, "getCycleProgress")
        super.getCycleProgress(index, listener)
    }

    override fun getCurrentCycleProgress(listener: Listener) {
        Log.v(LOG_TAG, "getCurrentCycleProgress")
        super.getCurrentCycleProgress(listener)
    }

    override fun getDayProgress(cycleIndex: Int, dayIndex: Int, listener: Listener) {
        Log.v(LOG_TAG, "getDayProgress")
        super.getDayProgress(cycleIndex, dayIndex, listener)
    }

    override fun getCurrentDayProgress(listener: Listener) {
        Log.v(LOG_TAG, "getCurrentDayProgress")
        super.getCurrentDayProgress(listener)
    }

    override fun getEarningOverview(cycleIndex: Int, dayIndex: Int, listener: Listener) {
        Log.v(LOG_TAG, "getEarningOverview cycle & day")
        getEarningOverview(listener)
    }

    override fun getEarningOverview(cycleIndex: Int, listener: Listener) {
        Log.v(LOG_TAG, "getEarningOverview cycle")
        getEarningOverview(listener)
    }

    override fun getEarningOverview(listener: Listener) {
        Log.v(LOG_TAG, "getEarningOverview")
        val overview = earningsController.getCurrentEarningsOverview()
        val overviewTree = gson.toJsonTree(overview)
        val response = createResponse("earnings", overviewTree)
        listener.onSuccess(response)
    }

    override fun getEarningDetails(listener: Listener) {
        Log.v(LOG_TAG, "getEarningDetails")
        val details = earningsController.getCurrentEarningsDetails()
        val detailsTree = gson.toJsonTree(details)
        val response = createResponse("earnings", detailsTree)
        listener.onSuccess(response)
    }

    private fun createResponse(key: String, jsonTree: JsonElement): RestResponse {
        val response = RestResponse()
        response.code = 200
        response.successful = true
        response.optional = JsonObject()
        response.optional.add(key, jsonTree)
        return response
    }

    override fun submitWakeSleepSchedule() {
        Log.v(LOG_TAG, "submitWakeSleepSchedule")
        val schedule = super.createWakeSleepSchedule()
        val json = serialize(schedule).toString()
        uploadJson(WAKE_SLEEP_TASK_IDENTIFIER, json, Instant.now())
        uploadReport(AvailabilityIdentifier, json)
    }

    override fun submitTestSchedule() {
        Log.v(LOG_TAG, "submitTestSchedule")
        val schedule = super.createTestSchedule()
        val json = serialize(schedule).toString()
        uploadJson(STUDY_PERIOD_SCHEDULE_TASK_IDENTIFIER, json, Instant.now())
        uploadReport(TestScheduleIdentifier, json)
    }

    override fun submitSignature(bitmap: Bitmap) {
        Log.v(LOG_TAG, "submitSignature")
        uploadImage(SIGNATURE_TASK_IDENTIFIER, bitmap)
    }

    override fun submitTest(session: TestSession?) {
        Log.v(LOG_TAG, "submitTestSession")
        val test = createTestSubmission(session)
        session?.purgeData()
        val startTime = session?.startTime?.toThreeTenInstant() ?: Instant.now()

        // Only add finished sessions, since missed sessions are also uploaded
        var totalEarnings: String? = null
        if (test.finished_session > 0) {
            completedTestManager.append(test)
            earningsController.completedTests = completedTestManager.current.completed
            totalEarnings = earningsController.calculateTotalEarnings()
        }

        uploadJson(TEST_SESSION_TASK_IDENTIFIER, serialize(test).toString(), startTime,
                createCurrentSessionMetaData(startTime, test, totalEarnings))
    }

    fun createCurrentSessionMetaData(startTime: Instant,
                                     test: TestSubmission,
                                     totalEarnings: String? = null): List<AnswerResultBase<Any>> {

        val endTime = Instant.now()
        val sessionMetadata = mutableListOf<AnswerResultBase<Any>>()

        sessionMetadata.add(AnswerResultBase(
                "week", startTime, endTime, test.week, AnswerResultType.INTEGER))
        sessionMetadata.add(AnswerResultBase(
                "day", startTime, endTime, test.day, AnswerResultType.INTEGER))
        sessionMetadata.add(AnswerResultBase(
                "session", startTime, endTime, test.session, AnswerResultType.INTEGER))

        val finished = (test.finished_session ?: 0) > 0
        sessionMetadata.add(AnswerResultBase(
                "finished", startTime, endTime, finished, AnswerResultType.BOOLEAN))

        totalEarnings?.let {
            sessionMetadata.add(AnswerResultBase(
                    "totalEarnings", startTime, endTime, it, AnswerResultType.STRING))
        }

        return sessionMetadata
    }

    override fun submitTest(test: TestSubmission) {
        Log.v(LOG_TAG, "TestSubmission")
        // No-op, this is handled by submitTest
    }

    private fun createAppTypeResult(): AnswerResultBase<String>? {
        return AnswerResultBase("app",
                Instant.now(), Instant.now(), "HASD", AnswerResultType.STRING)
    }

    private fun uploadJson(taskId: String, json: String, startTime: Instant,
                           optionalResults: List<AnswerResultBase<Any>>? = null) {

        // Need application context to write the file
        val appContext = Application.getInstance()?.appContext ?: run { return }

        appContext.openFileOutput("data.json", Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }

        val jsonFileResult = FileResultBase("data",
                startTime, startTime, JSON_MIME_CONTENT_TYPE,
                appContext.filesDir.path + File.separator + "data.json")

        var taskResultBase = TaskResultBase(taskId, startTime,
                Instant.now(), UUID.randomUUID(), Schema(taskId, 2),
                mutableListOf(), mutableListOf())

        taskResultBase = taskResultBase.addStepHistory(jsonFileResult)
        createAppTypeResult()?.let {
            taskResultBase = taskResultBase.addStepHistory(it)
        }
        optionalResults?.forEach {
            taskResultBase = taskResultBase.addStepHistory(it)
        }
        if (Study.getParticipant() != null && Study.getParticipant().id != null) {
            taskResultBase = taskResultBase.addStepHistory(
                    AnswerResultBase("arcId", startTime,
                            Instant.now(), Study.getParticipant().id, AnswerResultType.STRING))
        }

        uploadCompletableAsync(taskResultUploader.processTaskResult(taskResultBase),
                "Successfully uploaded $taskId to bridge",
                "Failed to upload $taskId to bridge, will try again later")
    }

    private fun uploadImage(taskId: String, bitmap: Bitmap,
                            optionalResults: List<AnswerResultBase<Any>>? = null) {
        val file = CacheManager.getInstance()
            .putBitmapReturnFile("data", bitmap, 50)
            ?: run { return }

        val fileResult = FileResultBase("data.png",
                Instant.now(), Instant.now(), PNG_MIME_CONTENT_TYPE, file.path)

        var taskResultBase = TaskResultBase(taskId, Instant.now(),
                Instant.now(), UUID.randomUUID(), Schema(taskId, 2),
                mutableListOf(), mutableListOf())

        taskResultBase = taskResultBase.addStepHistory(fileResult)
        createAppTypeResult()?.let {
            taskResultBase = taskResultBase.addStepHistory(it)
        }
        optionalResults?.forEach {
            taskResultBase = taskResultBase.addStepHistory(it)
        }
        if (Study.getParticipant() != null && Study.getParticipant().id != null) {
            taskResultBase = taskResultBase.addStepHistory(
                    AnswerResultBase("arcId", Instant.now(),
                            Instant.now(), Study.getParticipant().id, AnswerResultType.STRING))
        }

        uploadCompletableAsync(taskResultUploader.processTaskResult(taskResultBase),
                "Successfully uploaded $taskId to bridge",
                "Failed to upload $taskId to bridge, will try again later")
    }

    private fun loadHistoryFromBridge(listener: (data: ExistingData?, error: String?) -> Unit) {
        val data = ExistingData()
        val reportIds = listOf(
                AvailabilityIdentifier, TestScheduleIdentifier, CompletedTestsIdentifier)

        var successCtr = reportIds.size
        var hasThrownError = false

        reportIds.forEach { reportId ->
            getSingletonReport(reportId) { json, error ->
                error?.let {
                    if (hasThrownError) {
                        return@getSingletonReport
                    }
                    hasThrownError = true
                    listener.invoke(data, it)
                    return@getSingletonReport
                }

                val jsonUnwrapped = json ?: ""
                when (reportId) {
                    AvailabilityIdentifier -> parseWakeSleepSchedule(jsonUnwrapped, data)
                    TestScheduleIdentifier -> parseTestSchedule(jsonUnwrapped, data)
                    CompletedTestsIdentifier -> parseCompletedTests(jsonUnwrapped, data)
                }

                successCtr -= 1
                if (successCtr <= 0 && !hasThrownError) {  // Check for done state

                    // Save new study state after sign in if we had a previous account
                    if (data.isValid) {
                        val state = Study.getScheduler().getExistingParticipantState(data)
                        Study.getParticipant().state = state
                        Study.getScheduler().scheduleNotifications(
                                Study.getCurrentTestCycle(), false)
                    }

                    listener.invoke(data, null)
                }
            }
        }
    }

    private fun parseWakeSleepSchedule(json: String, data: ExistingData) {
        val wakeSleep = gson.fromJson(json, WakeSleepSchedule::class.java)
        data.wake_sleep_schedule = wakeSleep
    }

    private fun parseTestSchedule(json: String, data: ExistingData) {
        val schedule = gson.fromJson(json, TestSchedule::class.java) ?: run {
            return
        }

        data.test_schedule = schedule

        val twoHoursInMs = 60L * 60L * 2L
        val now = (System.currentTimeMillis() / 1000L) // time in seconds
        val schedules = schedule.sessions
                .sortedBy { it.session_date }
                .filter { (it.session_date + twoHoursInMs) < now }

        data.first_test = convertToSessionInfo(schedules.firstOrNull())
        data.latest_test = convertToSessionInfo(schedules.lastOrNull())
    }

    private fun parseCompletedTests(json: String, data: ExistingData) {
        val completed = gson.fromJson(json, CompletedTestList::class.java)
                ?: CompletedTestList(listOf())
        completedTestManager.mergeInList(completed)
        earningsController.completedTests = completedTestManager.current.completed
    }

    private fun convertToSessionInfo(testSession: TestScheduleSession?): SessionInfo? {
        val test = testSession ?: run { return null }
        val session = SessionInfo()
        session.session_date = test.session_date.toInt()
        session.week = test.week
        session.day = test.day
        session.session = test.session
        session.session_id = test.session_id.toInt()
        return session
    }

    open fun uploadReport(reportId: String, json: String, listener: Listener?) {
        val data = ReportData()
        data.localDate = reportSingletonLocalDate.toJodaLocalDate()
        data.data = json

        compositeSubscription.add(
                reportManager.saveReportJSON(reportId, data)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ message: Message? ->
                            Log.i(LOG_TAG, "Successfully uploaded $reportId to bridge")
                            listener?.onSuccess(createSuccessRestResponse())
                        }) { t: Throwable ->
                            Log.e(LOG_TAG, "Error uploading $reportId to bridge ${t.localizedMessage}")
                            Log.w(LOG_TAG, "Failed to upload $reportId to bridge")
                            listener?.onFailure(createFailureRestResponse(t))
                        })
    }

    override fun uploadReport(reportId: String, json: String) {
        uploadReport(reportId, json, null)
    }

    private fun getSingletonReport(reportId: String, listener: (json: String?, error: String?) -> Unit) {
        val start = reportSingletonLocalDate.toJodaLocalDate().minusDays(2)
        val end = reportSingletonLocalDate.toJodaLocalDate().plusDays(2)
        compositeSubscription.add(
                reportManager.getReportsV3(reportId, start, end)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ reports: ReportDataList? ->
                            Log.i(LOG_TAG, "Successful GET for $reportId on thread ${Thread.currentThread()}")
                            val json = (reports?.items?.firstOrNull()?.data as? String)
                            listener.invoke(json, null)
                        }) { t: Throwable ->
                            Log.e(LOG_TAG, "Failed to GET $reportId from bridge ${t.localizedMessage}")
                            listener.invoke(null, t.localizedMessage)
                        })
    }
}

/**
 * Used to mark the user as migrated (a.k.a signed into bridge and off HM server)
 */
data class MigratedStatus(var status: Boolean = false)