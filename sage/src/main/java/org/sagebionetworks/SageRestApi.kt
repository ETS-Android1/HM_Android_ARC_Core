/*
 * BSD 3-Clause License
 *
 * Copyright 2021  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sagebionetworks.research.sagearc

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.common.base.Strings.repeat
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.LinkedTreeMap
import com.healthymedium.arc.api.RestClient
import com.healthymedium.arc.api.RestResponse
import com.healthymedium.arc.api.models.*
import com.healthymedium.arc.core.Application
import com.healthymedium.arc.core.Device
import com.healthymedium.arc.study.Participant
import com.healthymedium.arc.study.Study
import com.healthymedium.arc.study.TestSession
import com.healthymedium.arc.utilities.CacheManager
import com.healthymedium.arc.utilities.PreferencesManager
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.swagger.annotations.Api
import org.joda.time.DateTimeZone
import org.sagebionetworks.bridge.android.manager.AuthenticationManager
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider
import org.sagebionetworks.bridge.android.manager.ParticipantRecordManager
import org.sagebionetworks.bridge.android.manager.UploadManager
import org.sagebionetworks.bridge.rest.model.*
import org.sagebionetworks.migration.PasswordGenerator
import org.sagebionetworks.research.domain.Schema
import org.sagebionetworks.research.domain.result.AnswerResultType
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase
import org.sagebionetworks.research.domain.result.implementations.FileResultBase
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase
import org.sagebionetworks.research.sagearc.SageRestApi.HmToSageMigration.Companion.fixParticipantId
import org.sagebionetworks.research.sagearc.SageRestApi.HmToSageMigration.Companion.migrationDataKey
import org.sagebionetworks.research.sageresearch.extensions.toJodaLocalDate
import org.sagebionetworks.research.sageresearch.extensions.toThreeTenInstant
import org.sagebionetworks.research.sageresearch.extensions.toThreeTenLocalDate
import org.sagebionetworks.research.sageresearch_app_sdk.TaskResultUploader
import org.threeten.bp.Instant
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.io.File
import java.util.*

open class SageRestApi(val reportManager: ParticipantRecordManager,
                       val authManager: AuthenticationManager,
                       val taskResultUploader: TaskResultUploader,
                       val uploadManager: UploadManager,
                       val earningsController: SageEarningsController):
        RestClient<Api>(null), ReportUploadListener {

    companion object {
        var LOG_TAG = SageRestApi::class.java.simpleName

        // The length of all Arc IDs
        const val PARTICIPANT_ID_LENGTH = 6

        const val SIGNATURE_TASK_IDENTIFIER = "Signature"
        const val TEST_SESSION_TASK_IDENTIFIER = "TestSession"
        const val STUDY_PERIOD_SCHEDULE_TASK_IDENTIFIER = "StudyPeriodSchedule"
        const val WAKE_SLEEP_TASK_IDENTIFIER = "WakeSleep"

        const val ATTRIBUTE_VERIFICATION_CODE = "VERIFICATION_CODE"
        const val ATTRIBUTE_IS_MIGRATED = "IS_MIGRATED"
        const val ATTRIBUTE_VALUE_TRUE = "true"

        const val JSON_MIME_CONTENT_TYPE = "application/json"
        const val PNG_MIME_CONTENT_TYPE = "image/png"

        const val DATA_FILE_NAME = "data"
        const val JSON_UPLOAD_FILE_NAME = "$DATA_FILE_NAME.json"
        const val PNG_UPLOAD_FILE_NAME = "$DATA_FILE_NAME.png"

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
            listener?.onSuccess(createSuccessRestResponse())
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
                var errorStr: String? = null;
                when (reportId) {
                    AvailabilityIdentifier -> errorStr = parseWakeSleepSchedule(jsonUnwrapped, data)
                    TestScheduleIdentifier -> errorStr = parseTestSchedule(jsonUnwrapped, data)
                    CompletedTestsIdentifier -> errorStr = parseCompletedTests(jsonUnwrapped, data)
                }

                errorStr?.let {
                    if (hasThrownError) {
                        return@getSingletonReport
                    }
                    hasThrownError = true
                    listener.invoke(data, it)
                    return@getSingletonReport
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

    private fun parseWakeSleepSchedule(json: String?, data: ExistingData): String? {
        try {
            val wakeSleep = gson.fromJson(json, WakeSleepSchedule::class.java)
            data.wake_sleep_schedule = wakeSleep
        } catch (e: JsonSyntaxException) {
            return "WakeSleep invalid data format"
        }
        return null
    }

    private fun parseTestSchedule(json: String?, data: ExistingData): String? {
        try {
            val schedule = gson.fromJson(json, TestSchedule::class.java)

            data.test_schedule = schedule

            // Exit early if we don't have any first tests to find
            if (schedule == null) {
                return null;
            }

            val twoHoursInMs = 60L * 60L * 2L
            val now = (System.currentTimeMillis() / 1000L) // time in seconds
            val schedules = schedule.sessions
                    .sortedBy { it.session_date }
                    .filter { (it.session_date + twoHoursInMs) < now }

            data.first_test = convertToSessionInfo(schedules.firstOrNull())
            data.latest_test = convertToSessionInfo(schedules.lastOrNull())
        } catch (e: JsonSyntaxException) {
            return "TestSchedule invalid data format"
        }
        return null;
    }

    private fun parseCompletedTests(json: String?, data: ExistingData): String? {
        try {
            val completed = gson.fromJson(json, CompletedTestList::class.java)
            completedTestManager.setCurrentList(completed ?: CompletedTestList(listOf()))
            earningsController.completedTests = completedTestManager.current.completed
        } catch (e: JsonSyntaxException) {
            return "CompletedTestList invalid data format"
        }
        return null
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

    protected fun getSingletonReport(reportId: String, listener: (json: String?, error: String?) -> Unit) {
        val start = reportSingletonLocalDate.toJodaLocalDate().minusDays(2)
        val end = reportSingletonLocalDate.toJodaLocalDate().plusDays(2)
        compositeSubscription.add(
                reportManager.getReportsV3(reportId, start, end)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ reports: ReportDataList? ->
                            Log.i(LOG_TAG, "Successful GET for $reportId on thread ${Thread.currentThread()}")
                            // This is how the JSON is stored when uploaded by the iOS/Android app
                            var json = (reports?.items?.firstOrNull()?.data as? String)
                            // This is how the JSON will show up if edited through Bridge
                            (reports?.items?.firstOrNull()?.data as? LinkedTreeMap<*, *>)?.let {
                                json = gson.toJson(it)
                            }
                            listener.invoke(json, null)
                        }) { t: Throwable ->
                            Log.e(LOG_TAG, "Failed to GET $reportId from bridge ${t.localizedMessage}")
                            listener.invoke(null, t.localizedMessage)
                        })
    }

    /**
     * @return true if the user needs to migrate from HM to Sage bridge server, false otherwise
     */
    public fun userNeedsToMigrate(): Boolean {
        val dataMigration = loadMigrationState()
        // We were in the middle of a migration, when something went wrong, continue
        if (dataMigration != null) {
            return true
        }
        return HmToSageMigration.userNeedsToMigrate(Study.getParticipant(),
                BridgeManagerProvider.getInstance().accountDao.externalId)
    }

    public fun migrateUserToSageBridge(completionListener: MigrationCompletedListener) {
        val arcId = fixParticipantId(Study.getParticipant().state.id)
        val dataMigration = loadMigrationState() ?:
            MigrationData.initial(arcId, Device.getId())
        migrateUser(completionListener, dataMigration)
    }

    /**
     * Migrating existing users
     * A user is existing if they have an Arc ID, a site location, and a Device ID.
     *
     * When the HM user opens the app after updating to Sage's app,
     * they will not have their account created yet with their Arc ID as their External ID.
     * Instead, the migration code will have created an External ID that is their Device ID
     * and the password for this account will also be their Device ID.
     *
     * The migration code marks this user with the test_user data group,
     * has their user attribute IS_MIGRATED set to false,
     * and has populated the account with that user’s data.
     */
    class HmToSageMigration {
        companion object {
            val migrationDataKey = "migrationDataKey"
            val migrationSteps = 10

            public fun userNeedsToMigrate(participant: Participant?, externalId: String?): Boolean {
                if (participant == null ||
                        participant.state == null ||
                        participant.state.id == null) {
                    return false
                }
                val arcId = fixParticipantId(participant.state.id)
                // A user needs to migrate, if they have previously been assigned an Arc ID,
                // But they are not signed into Bridge with their External ID equal to their Arc ID.
                return arcId != externalId
            }

            /**
             * @param participantId raw participant ID from HM data model file
             * this may be any length 0 to 6
             * @return the participant ID with a length of 6, with leading "0"s added
             */
            public fun fixParticipantId(participantId: String): String {
                if (participantId.length >= PARTICIPANT_ID_LENGTH) {
                    return participantId.substring(0, PARTICIPANT_ID_LENGTH)
                }
                val zerosToAdd: Int = PARTICIPANT_ID_LENGTH - participantId.length
                val zerosPrefix: String = repeat("0", zerosToAdd)
                return zerosPrefix + participantId
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    public fun saveMigrationStateImmediately(data: MigrationData) {
        PreferencesManager.getInstance().putStringImmediately(migrationDataKey, gson.toJson(data))
    }

    public fun loadMigrationState(): MigrationData? {
        val dataJson = PreferencesManager.getInstance().getString(migrationDataKey, null)
        dataJson?.let {
            return gson.fromJson(dataJson, MigrationData::class.java)
        }
        return null
    }

    private fun saveAndContinueMigration(completionListener: MigrationCompletedListener,
                                         newMigration: MigrationData) {
        Handler(Looper.getMainLooper()).postDelayed({
            saveMigrationStateImmediately(newMigration)
            migrateUser(completionListener, newMigration)
        }, 100) // Wait a 100 milliseconds for Bridge SDK to finish its moves
    }

    private fun migrationError(completionListener: MigrationCompletedListener, errorStr: String) {
        val arcID = fixParticipantId(Study.getParticipant().state.id)
        val deviceId = Device.getId()
        Log.e(LOG_TAG, errorStr)
        completionListener.failure("$errorStr\n$arcID\n$deviceId")
    }

    public fun migrateUser(completionListener: MigrationCompletedListener,
                           migration: MigrationData) {

        var progressCtr = 0

        // First step of migration, sign-in to using device-id and save user attributes
        progressCtr += 1
        if (migration.studyId == null || migration.attributes == null) {
            completionListener.progressUpdate(progressCtr)
            val what = "signing in with device-id "
            Log.i(LOG_TAG, what)
            compositeSubscription.add(
                    authManager.signInWithExternalId(migration.deviceId, migration.deviceId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ userSessionInfo: UserSessionInfo? ->
                                saveAndContinueMigration(completionListener, migration.copy(
                                        studyId = userSessionInfo?.externalIds?.keys?.firstOrNull(),
                                        attributes = userSessionInfo?.attributes ?: mapOf()))
                            }) { t: Throwable ->
                                migrationError(completionListener,
                                        "Error $what ${t.localizedMessage}")
                            })
            return
        }

        // Next step of migration, load the Completed Tests user report
        progressCtr += 1
        if (migration.completedTestJson == null) {
            completionListener.progressUpdate(progressCtr)
            val what = "getting CompletedTestsIdentifier report "
            Log.i(LOG_TAG, what)
            getSingletonReport(CompletedTestsIdentifier) { json, error ->
                error?.let {
                    completionListener.failure("Error $what $it")
                    return@getSingletonReport
                }
                saveAndContinueMigration(completionListener, migration.copy(
                        completedTestJson = json ?: ""))
            }
            return
        }

        // Next step of migration, load the test sessions schedules user report
        progressCtr += 1
        if (migration.sessionScheduleJson == null) {
            completionListener.progressUpdate(progressCtr)
            val what = "getting TestScheduleIdentifier report "
            Log.i(LOG_TAG, what)
            getSingletonReport(TestScheduleIdentifier) { json, error ->
                error?.let {
                    completionListener.failure("Error $what $it")
                    return@getSingletonReport
                }
                saveAndContinueMigration(completionListener, migration.copy(
                        sessionScheduleJson = json ?: ""))
            }
            return
        }

        // Next step of migration, load the wake sleep schedule user report
        progressCtr += 1
        if (migration.wakeSleepScheduleJson == null) {
            completionListener.progressUpdate(progressCtr)
            val what = "getting AvailabilityIdentifier report "
            Log.i(LOG_TAG, what)
            getSingletonReport(AvailabilityIdentifier) { json, error ->
                error?.let {
                    completionListener.failure("Error $what $it")
                    return@getSingletonReport
                }
                saveAndContinueMigration(completionListener, migration.copy(
                        wakeSleepScheduleJson = json ?: ""))
            }
            return
        }

        // Next step is to mark the device-id user as migrated
        progressCtr += 1
        if (migration.isMigrated == null) {
            completionListener.progressUpdate(progressCtr)
            val bridgeUser = StudyParticipant()
            val attributes = migration.attributes.toMutableMap()
            attributes[ATTRIBUTE_IS_MIGRATED] = ATTRIBUTE_VALUE_TRUE
            bridgeUser.attributes = attributes

            val what = "marking Device ID user as migrated "
            Log.i(LOG_TAG, what)
            compositeSubscription.add(
                    reportManager.updateParticipantRecord(bridgeUser)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ userSessionInfo: UserSessionInfo? ->
                                saveAndContinueMigration(completionListener, migration.copy(
                                        isMigrated = true))
                            }) { t: Throwable ->
                                migrationError(completionListener,
                                        "Error $what ${t.localizedMessage}")
                            })
            return
        }

        // Next step is to sign out of Bridge
        progressCtr += 1
        if (migration.isSignedOutOfDeviceId == null) {
            completionListener.progressUpdate(progressCtr)
            val what = "signing out of Device ID user "
            Log.i(LOG_TAG, what)
            compositeSubscription.add(
                    authManager.signOut()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                saveAndContinueMigration(completionListener, migration.copy(
                                        isSignedOutOfDeviceId = true))
                            }) { t: Throwable ->
                                // If we aren't signed in anyways, we can consider this step complete
                                if (t.localizedMessage == "Not signed in") {
                                    saveAndContinueMigration(completionListener, migration.copy(
                                            isSignedOutOfDeviceId = true))
                                    return@subscribe
                                }
                                migrationError(completionListener,
                                        "Error $what ${t.localizedMessage}")
                            })
            return
        }

        // Next step is to create a new user on Bridge with Arc ID, and a secure random password
        progressCtr += 1
        if (migration.newUserPassword == null) {
            completionListener.progressUpdate(progressCtr)
            val signUp = SignUp()
            val password = PasswordGenerator.INSTANCE.nextPassword();
            signUp.password = password
            signUp.sharingScope = SharingScope.ALL_QUALIFIED_RESEARCHERS

            val attributes = migration.attributes.toMutableMap()
            attributes[ATTRIBUTE_VERIFICATION_CODE] = password
            attributes[ATTRIBUTE_IS_MIGRATED] = "" // remove migration status
            signUp.attributes = attributes

            val arcId = migration.arcId
            signUp.externalIds = mapOf(Pair(migration.studyId, arcId))

            val what = "signing up Arc ID user "
            Log.i(LOG_TAG, what)
            compositeSubscription.add(
                    authManager.signUp(signUp)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                saveAndContinueMigration(completionListener, migration.copy(
                                        newUserPassword = password))
                            }) { t: Throwable ->
                                migrationError(completionListener,
                                        "Error $what ${t.localizedMessage}")
                            })
            return
        }

        // First step of migration, sign-in to using device-id and save user attributes
        progressCtr += 1
        if (migration.isNewUserAuthenticated == null) {
            val arcId = migration.arcId
            val password = migration.newUserPassword
            completionListener.progressUpdate(progressCtr)
            val what = "signing in with Arc ID "
            Log.i(LOG_TAG, what)
            compositeSubscription.add(
                    authManager.signInWithExternalId(arcId, password)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ userSessionInfo: UserSessionInfo? ->
                                saveAndContinueMigration(completionListener, migration.copy(
                                        isNewUserAuthenticated = true))
                            }) { t: Throwable ->
                                migrationError(completionListener,
                                        "Error $what ${t.localizedMessage}")
                            })
            return
        }

        // Next step is to upload the completed test user report
        progressCtr += 1
        if (migration.completedTestUploaded == null) {
            completionListener.progressUpdate(progressCtr)

            val what = "uploading CompletedTestsIdentifier report "
            Log.i(LOG_TAG, what)
            uploadReport(CompletedTestsIdentifier, migration.completedTestJson, object : Listener {
                override fun onSuccess(response: RestResponse?) {
                    // If there is data we should parse it and send it to the earnings controller
                    if (migration.completedTestJson.isNotEmpty()) {
                        val newList = gson.fromJson(migration.completedTestJson,
                                CompletedTestList::class.java)
                        // This automatically syncs the completed tests to bridge and saves it locally
                        completedTestManager.setCurrentList(newList)
                        earningsController.completedTests = completedTestManager.current.completed
                    }
                    saveAndContinueMigration(completionListener, migration.copy(
                            completedTestUploaded = true
                    ))
                }

                override fun onFailure(response: RestResponse?) {
                    migrationError(completionListener,
                            "Error $what ${response?.errors.toString()}")
                }
            })
            return
        }

        // Next step is to upload the test session schedule report
        progressCtr += 1
        if (migration.sessionScheduleUploaded == null) {
            completionListener.progressUpdate(progressCtr)

            val what = "uploading TestScheduleIdentifier report "
            Log.i(LOG_TAG, what)
            uploadReport(TestScheduleIdentifier, migration.sessionScheduleJson, object : Listener {
                override fun onSuccess(response: RestResponse?) {
                    saveAndContinueMigration(completionListener, migration.copy(
                            sessionScheduleUploaded = true
                    ))
                }

                override fun onFailure(response: RestResponse?) {
                    migrationError(completionListener,
                            "Error $what ${response?.errors.toString()}")
                }
            })
            return
        }

        // Last step is to upload the test session schedule report
        progressCtr += 1
        if (migration.wakeSleepScheduleUploaded == null) {
            completionListener.progressUpdate(progressCtr)

            val what = "uploading AvailabilityIdentifier report "
            Log.i(LOG_TAG, what)
            uploadReport(AvailabilityIdentifier, migration.wakeSleepScheduleJson, object : Listener {
                override fun onSuccess(response: RestResponse?) {
                    saveAndContinueMigration(completionListener, migration.copy(
                            wakeSleepScheduleUploaded = true
                    ))
                }

                override fun onFailure(response: RestResponse?) {
                    migrationError(completionListener,
                            "Error $what in ${response?.errors.toString()}")
                }
            })
            return
        }

        // Remove traces of successful migration
        PreferencesManager.getInstance().removeImmediately(migrationDataKey)
        // We are done with migration!
        completionListener.success()
    }

    public interface MigrationCompletedListener {
        public fun progressUpdate(progress: Int)
        public fun success()
        public fun failure(errorString: String)
    }

    public data class MigrationData(
            val arcId: String,
            val deviceId: String,
            val studyId: String?,
            val attributes: Map<String, String>?,
            val completedTestJson: String?,
            val sessionScheduleJson: String?,
            val wakeSleepScheduleJson: String?,
            val isMigrated: Boolean?,
            val isSignedOutOfDeviceId: Boolean?,
            val newUserPassword: String?,
            val isNewUserAuthenticated: Boolean?,
            val completedTestUploaded: Boolean?,
            val sessionScheduleUploaded: Boolean?,
            val wakeSleepScheduleUploaded: Boolean?) {

        companion object {
            fun initial(arcId: String, deviceId: String): MigrationData {
                return MigrationData(arcId, deviceId,
                        null, null, null,
                        null, null, null,
                        null, null, null,
                        null, null, null)
            }
        }
    }
}