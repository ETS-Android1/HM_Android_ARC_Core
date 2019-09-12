package com.healthymedium.arc.study;

import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.utilities.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TestCycle {

    private int id;

    private DateTime scheduledStartDate;          // official start date of the TestCycle,
    private DateTime scheduledEndDate;            // official end date of the TestCycle,
    private DateTime actualStartDate;           // user-modified start date of the TestCycle,
    private DateTime actualEndDate;             // user-modified end date of the TestCycle,

    private List<TestDay> days;

    public TestCycle(int id, DateTime start, DateTime end) {
        this.id = id;
        scheduledStartDate = JodaUtil.setMidnight(start);
        scheduledEndDate = JodaUtil.setMidnight(end);
        days = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public DateTime getScheduledStartDate() {
        return scheduledStartDate;
    }

    public DateTime getScheduledEndDate() {
        return scheduledEndDate;
    }

    public DateTime getActualStartDate() {
        if(actualStartDate == null) {
            return scheduledStartDate;
        }
        return actualStartDate;
    }

    public void setActualStartDate(DateTime actualStartDate) {
        this.actualStartDate = JodaUtil.setMidnight(actualStartDate);
    }

    public DateTime getActualEndDate() {
        if(actualEndDate == null) {
            return scheduledEndDate;
        }
        return actualEndDate;
    }

    public void setActualEndDate(DateTime actualEndDate) {
        this.actualEndDate = JodaUtil.setMidnight(actualEndDate);
    }

    public List<TestDay> getTestDays() {
        return days;
    }

    public TestDay getTestDay(int dayIndex) {
        if(dayIndex>=days.size()) {
            return null;
        }
        return days.get(dayIndex);
    }

    public List<TestSession> getTestSessions() {
        List<TestSession> sessions = new ArrayList<>();
        for(TestDay day : days) {
            sessions.addAll(day.sessions);
        }
        return sessions;
    }

    public TestSession getTestSession(int index) {
        int pointer = 0;
        for(TestDay day : days) {
            for(TestSession session : day.sessions) {
                if(pointer==index) {
                    return session;
                }
                pointer++;
            }
        }
        return null;
    }

    public int getNumberOfTestDays() {
        return days.size();
    }

    public int getNumberOfTests() {
        int count = 0;
        for(TestDay day : days) {
            count += day.getNumberOfTests();
        }
        return count;
    }

    public int getNumberOfTestsLeft() {
        int count = 0;
        for(TestDay day : days) {
            count += day.getNumberOfTestsLeft();
        }
        return count;
    }

    public boolean isOver() {
        if(getActualEndDate().isBeforeNow()) {
            return true;
        }
        if(days.size()==0) {
            return true;
        }
        int last = days.size()-1;
        return days.get(last).isOver();
    }

    public boolean hasStarted() {
        if(days.size()==0) {
            return false;
        }
        return days.get(0).hasStarted();
    }

    public boolean hasThereBeenAFinishedTest() {
        for(TestDay day : days) {
            if(day.getNumberOfTestsFinished()>0) {
                return true;
            }
        }
        return false;
    }

    public int getProgress() {
        float progress = 0;
        int numEntries = days.size();
        for(TestDay day : days){
            progress += ((float)day.getProgress()/numEntries);
        }
        return (int) progress;
    }

}
