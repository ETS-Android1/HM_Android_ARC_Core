package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.api.tests.BaseTest;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSession {

    private int dayIndex;
    private int index;
    private int id;
    private LocalDate scheduledDate;    // The user-modified scheduled date
    private DateTime prescribedTime;    // The original scheduled time

    private DateTime startTime;
    private DateTime completeTime;

    private List<Object> testData = new ArrayList<>();

    private boolean finishedSession;
    private boolean missedSession;
    private int interrupted;

    public TestSession(int dayIndex, int index, int id){
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

    public int getId() {
        return id;
    }

    public boolean isFinished(){
        return finishedSession;
    }

    public void markCompleted(){
        completeTime = DateTime.now();
        finishedSession = true;
    }

    public void markAbandoned() {
        completeTime = DateTime.now();
        finishedSession = false;
    }

    public void markMissed(){
        missedSession = true;
        finishedSession = false;
    }

    public DateTime getExpirationTime() {
        if(scheduledDate!=null){
            return getPrescribedTime().withDate(scheduledDate).plusHours(2);
        }
        return getPrescribedTime().plusHours(2);
    }

    public DateTime getScheduledTime() {
        if(scheduledDate!=null){
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
        if(prescribedTime==null){
            throw new UnsupportedOperationException("Prescribed time was not set");
        }
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
    }

    public void addTestData(BaseTest data){
        Log.i("TestSession","addTestData("+data.getClass().getName()+")");
        testData.add(data);
    }

    public boolean isOver(){
        return (completeTime!=null || wasMissed());
    }

    public boolean isOngoing() {
        return startTime!=null && completeTime==null && !missedSession;
    }

    public boolean isAvailable(){
        DateTime now = DateTime.now();
        return (getScheduledTime().isBefore(now) && getExpirationTime().isAfter(now));
    }

    public List<Object> getTestData(){
        return testData;
    }

    public List<Object> getCopyOfTestData(){
        return Arrays.asList(Arrays.copyOf(testData.toArray(), testData.size()));
    }

    public int wasInterrupted() {
        return interrupted;
    }

    public boolean wasFinished() {
        return finishedSession;
    }

    public boolean wasMissed() {
        return missedSession;
    }

    public void markInterrupted(boolean interrupted) {
        this.interrupted = interrupted?1:0;
    }

//    public int getDayOfWeek() {
//        int dayOfWeek = getScheduledTime().getDayOfWeek();
//        // sun = 7
//        // mon = 1
//        // tue = 2
//        // wed = 3
//        // thu = 4
//        // fri = 5
//        // sat = 6
//        if(dayOfWeek==7){
//            dayOfWeek = 0;
//        }
//        // sun = 0
//        // mon = 1
//        // tue = 2
//        // wed = 3
//        // thu = 4
//        // fri = 5
//        // sat = 6
//        return dayOfWeek;
//    }

    public void purgeData(){
        testData.clear();
        interrupted = -99;
    }

}
