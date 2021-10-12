package com.healthymedium.arc.study;

import android.util.Log;
import org.joda.time.DateTime;
import java.util.List;

public class TestDay {

    int dayIndex;
    List<TestSession> sessions;
    DateTime start;
    DateTime end;

    public TestDay(int index, List<TestSession> sessions) {
        this.dayIndex = index;
        this.sessions = sessions;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public List<TestSession> getTestSessions() {
        return sessions;
    }

    public TestSession getTestSession(int index) {
        return sessions.get(index);
    }

    public int getNumberOfTestsAvailableNow() {
        int count = 0;
        for(TestSession session : sessions) {
            if(!session.isOver() && session.isAvailableNow()) {
                count++;
            }
        }
        return count;
    }

    public int getNumberOfTests() {
        return sessions.size();
    }

    public int getNumberOfTestsLeft() {
        int count = 0;
        for(TestSession session : sessions) {
            if(!session.isOver()) {
                count++;
            }
        }
        return count;
    }

    public int getNumberOfTestsFinished() {
        int count = 0;
        for(TestSession session : sessions) {
            if(session.isFinished()) {
                count++;
            }
        }
        return count;
    }

    public boolean hasThereBeenAFinishedTest() {
        return getNumberOfTestsFinished()>0;
    }

    public boolean isOver() {
        if(sessions.size()==0) {
            return true;
        }
        int last = sessions.size()-1;
        return sessions.get(last).isOver();
    }

    public boolean hasStarted() {
        if(sessions.size()==0) {
            return false;
        }
        DateTime scheduledTime = sessions.get(0).getScheduledTime();
        if(scheduledTime==null) {
           return false;
        }
        return scheduledTime.isBeforeNow();
    }

    public int getProgress() {
        float progress = 0;
        int numEntries = sessions.size();
        for(TestSession session : sessions){
            progress += ((float)session.getProgress()/numEntries);
        }
        return (int) progress;
    }

    public void setStartTime(DateTime start) {
        this.start = start;
    }

    public DateTime getStartTime() {
        return start;
    }

    public void setEndTime(DateTime end) {
        this.end = end;
    }

    public DateTime getEndTime() {
        return end;
    }

    public boolean isScheduleCorrupted() {
        DateTime start = getStartTime();
        DateTime end = getEndTime();

        DateTime before = null;
        for (TestSession session : sessions) {
            DateTime date = session.getScheduledTime();
            if(before!=null) {
                if(date.isBefore(before)) {
                    Log.e("TestDay","corruption found: sessions are out of order");
                    return true;
                }
            }
            before = date;

            if (date.isBefore(start) || date.isAfter(end)) {
                Log.e("TestDay","corruption found: sessions are out of testing window");
                return true;
            }
        }

        return false;
    }

}
