package com.healthymedium.arc.study;

import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.api.tests.BaseTest;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.healthymedium.arc.notifications.types.TestMissed.TAG_TEST_MISSED_COUNT;

public class TestSession {

    private int dayIndex;
    private int index;
    private int id;
    private LocalDate scheduledDate;    // The user-modified scheduled date
    private DateTime prescribedTime;    // The original scheduled time

    private DateTime startTime;
    private DateTime completeTime;

    private List<Object> testData = new ArrayList<>();
    private List<Integer> testPercentages = new ArrayList<>();

    private boolean finishedSession;
    private boolean missedSession;
    private boolean interrupted;

    public TestSession(int dayIndex, int index, int id) {
        this.dayIndex = dayIndex;
        this.index = index;
        this.id = id;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public int getIndex() {
        return index;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isFinished() {
        return finishedSession;
    }

    public void markCompleted() {
        completeTime = DateTime.now();
        finishedSession = true;
    }

    public void markAbandoned() {
        completeTime = DateTime.now();
        finishedSession = false;
    }

    public void markMissed() {
        missedSession = true;
        finishedSession = false;
    }

    public int getProgress() {
        float progress = 0;
        int numEntries = testPercentages.size();
        for(Integer percentage : testPercentages){
            progress += ((float)percentage/numEntries);
        }
        return (int) progress;
    }

    public DateTime getExpirationTime() {
        if(scheduledDate!=null) {
            return getPrescribedTime().withDate(scheduledDate).plusHours(2);
        }
        return getPrescribedTime().plusHours(2);
    }

    public DateTime getScheduledTime() {
        if(scheduledDate!=null) {
            return getPrescribedTime().withDate(scheduledDate);
        }
        return getPrescribedTime();
    }

    public DateTime getCompletedTime() {
        return completeTime;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public DateTime getPrescribedTime() {
        return prescribedTime;
    }

    public void setPrescribedTime(DateTime prescribedTime) {
        this.prescribedTime = prescribedTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void markStarted() {
        this.startTime = DateTime.now();

        // this null check lets unit tests work properly
        if(PreferencesManager.getInstance()!=null) {
            PreferencesManager.getInstance().putInt(TAG_TEST_MISSED_COUNT, 0);
        }
    }

    public void addTestData(BaseTest data) {
        Log.i("TestSession","addTestData("+data.getClass().getName()+")");
        testData.add(data);
        testPercentages.add(data.getProgress());
    }

    public boolean isOver() {
        return (completeTime!=null || wasMissed());
    }

    public boolean isOngoing() {
        return startTime!=null && completeTime==null && !missedSession;
    }

    public boolean isAvailableNow() {
        DateTime now = DateTime.now();
        return (getScheduledTime().isBefore(now) && getExpirationTime().isAfter(now));
    }

    public List<Object> getTestData() {
        return testData;
    }

    public List<Object> getCopyOfTestData(){
        return Arrays.asList(Arrays.copyOf(testData.toArray(), testData.size()));
    }

    public boolean wasInterrupted() {
        return interrupted;
    }

    public boolean wasFinished() {
        return finishedSession;
    }

    public boolean wasMissed() {
        return missedSession;
    }

    public void markInterrupted() {
        this.interrupted = true;
    }

    public void purgeData(){
        testData.clear();
        interrupted = false;
    }

}
