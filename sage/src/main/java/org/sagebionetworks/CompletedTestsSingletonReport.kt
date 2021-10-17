package org.sagebionetworks.research.sagearc

import com.google.gson.Gson
import com.healthymedium.arc.api.models.TestSubmission
import com.healthymedium.arc.utilities.PreferencesManager

public interface ReportUploadListener {
    fun uploadReport(reportId: String, json: String)
}

open class CompletedTestsSingletonReport(val reportListener: ReportUploadListener) {

    private val gson = Gson()

    init {
        PreferencesManager.getInstance().getString("CompletedTests", null)?.let {
            current = gson.fromJson(it, CompletedTestList::class.java)
        } ?: run {
            current = CompletedTestList(listOf())
        }
    }

    public lateinit var current: CompletedTestList

    public fun setCurrentList(newCurrent: CompletedTestList) {
        current = newCurrent
        val jsonStr = gson.toJson(newCurrent)
        PreferencesManager.getInstance().putString("CompletedTests", jsonStr)
    }

    open fun appendAll(testList: List<CompletedTest>) {
        val newList = current.completed.toMutableList()
        newList.addAll(testList)
        setCurrentList(CompletedTestList(newList))
        syncToBridge()
    }

    open fun append(session: TestSubmission) {
        val now = System.currentTimeMillis().toDouble() / 1000.0
        val test = CompletedTest(session.week, session.day, session.session, now)
        appendAll(listOf(test))
    }

    open fun syncToBridge() {
        val jsonStr = gson.toJson(current)
        reportListener.uploadReport(SageRestApi.CompletedTestsIdentifier, jsonStr)
    }

    open fun mergeInList(newList: CompletedTestList) {
        val filtered = newList.completed.filter {
            !current.completed.contains(it)
        }
        if (filtered.isNotEmpty()) {
            appendAll(filtered)
        }
    }
}

public data class CompletedTestList(
    val completed: List<CompletedTest>
)

public data class CompletedTest(
    val week: Int,
    val day: Int,
    val session: Int,
    val completedOn: Double
)