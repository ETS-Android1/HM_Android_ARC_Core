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