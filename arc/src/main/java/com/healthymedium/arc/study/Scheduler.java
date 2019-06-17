package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.api.models.ExistingData;
import com.healthymedium.arc.api.models.SessionInfo;
import com.healthymedium.arc.api.models.TestScheduleSession;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.time.JodaUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;

import java.util.List;
import java.util.Random;

public class Scheduler {

    protected static String tag = "Scheduler";

    public Scheduler() {

    }

    public void unscheduleNotifications(Visit visit) {
        if(visit==null){
            return;
        }

        NotificationManager notificationManager = NotificationManager.getInstance();
        int visitId = visit.getId();
        int testCount = visit.getNumberOfTests();

        if(Config.ENABLE_VIGNETTES){
            notificationManager.removeNotification(visitId, NotificationManager.VISIT_NEXT_MONTH);
            notificationManager.removeNotification(visitId, NotificationManager.VISIT_NEXT_WEEK);
            notificationManager.removeNotification(visitId, NotificationManager.VISIT_NEXT_DAY);
        }

        List<TestSession> testSessions = visit.getTestSessions();
        for(TestSession session: testSessions) {
            int sessionId = session.getId();
            Log.i(tag, "Unscheduling notifications for " + visitId + "." + sessionId);
            notificationManager.removeNotification(sessionId, NotificationManager.TEST_TAKE);
            notificationManager.removeNotification(testCount+sessionId, NotificationManager.TEST_MISSED);
        }
    }

    public void scheduleNotifications(Visit visit, boolean rescheduleDuringVisit) {
        if(visit==null){
            return;
        }

        NotificationManager notificationManager = NotificationManager.getInstance();
        int visitId = visit.getId();

        if (Config.ENABLE_VIGNETTES && visitId != 0 && !rescheduleDuringVisit) {
            Log.i(tag, "Scheduling vignette notifications for " + visitId + ".");
            DateTime startDate = visit.getActualStartDate();

            // one month out, noon
            DateTime monthVignette = startDate.minusMonths(1).plusHours(12);
            if (monthVignette.isAfterNow()) {
                notificationManager.scheduleNotification(visitId, NotificationManager.VISIT_NEXT_MONTH, monthVignette);
            }

            // one week out, noon
            DateTime weekVignette = startDate.minusWeeks(1).plusHours(12);
            if (weekVignette.isAfterNow()) {
                notificationManager.scheduleNotification(visitId, NotificationManager.VISIT_NEXT_WEEK, weekVignette);
            }

            // one day out, noon
            DateTime dayVignette = startDate.minusDays(1).plusHours(12);
            if (dayVignette.isAfterNow()) {
                notificationManager.scheduleNotification(visitId, NotificationManager.VISIT_NEXT_DAY, dayVignette);
            }
        }

        List<TestSession> testSessions = visit.getTestSessions();
        for (TestSession session : testSessions) {
            int sessionId = session.getId();
            Log.i(tag, "Scheduling notifications for " + visitId + "." + sessionId);

            if (session.getScheduledTime().isAfterNow()) {
                notificationManager.scheduleNotification(sessionId, NotificationManager.TEST_TAKE, session.getScheduledTime());
            }

            if(session.getExpirationTime().isAfterNow()){
                notificationManager.scheduleNotification(sessionId, NotificationManager.TEST_MISSED, session.getExpirationTime());
            }
        }

        visit.confirmNotificationsScheduled();
    }

    public void scheduleTests(DateTime now, Participant participant){
        ParticipantState state = participant.getState();
        if(state.visits.size()==0){
            initializeVisits(now,participant);
        }

        int size = state.visits.size();
        for(int i=state.currentVisit;i<size;i++){
                scheduleTestsForVisit(participant.getCircadianClock(), state, state.visits.get(i), false);
                state.visits.get(i).logTests();
        }

        participant.state.hasValidSchedule = true;
        participant.state.isStudyRunning = true;
        participant.save();
    }

    public void rescheduleTests(DateTime now, Participant participant){
        ParticipantState state = participant.getState();
        if(state.visits.size()==0){
            initializeVisits(now,participant);
        }

        int size = state.visits.size();
        for(int i=state.currentVisit;i<size;i++){
            scheduleTestsForVisit(participant.getCircadianClock(), state, state.visits.get(i), true);
            state.visits.get(i).logTests();
        }

        participant.state.hasValidSchedule = true;
        participant.state.isStudyRunning = true;
        participant.save();
    }

    public void initializeVisits(DateTime now, Participant participant) {
        List<Visit> visits = participant.state.visits;
        DateTime midnight = JodaUtil.setMidnight(now);

        Visit visit = new Visit(0,midnight,midnight.plusDays(1));
        TestSession testSession = new TestSession(0,0,0);
        testSession.setScheduledTime(midnight);
        visit.getTestSessions().add(testSession);
        visits.add(visit);
    }

    protected void scheduleTestsForVisit(CircadianClock clock, ParticipantState state, Visit visit) {
        Random random = new Random(System.currentTimeMillis());
        List<TestSession> testSessions = visit.testSessions;

        DateTime startDate = visit.getActualStartDate();
        List<CircadianInstant> orderedPairs = clock.getRhythmInstances(startDate.toLocalDate());
        boolean isCurrentVisit = visit.getId()==state.currentVisit;
        int currentDayIndex = getDayIndex(state.currentVisit,state.currentTestSession);
        int numDays = visit.getNumberOfDays();
        int index = 0;

        for(int i=0;i<numDays;i++){
            DateTime wake = orderedPairs.get(i).getWakeTime();
            DateTime bed = orderedPairs.get(i).getBedTime();

            boolean isCurrentDay = isCurrentVisit && (index==currentDayIndex) && LocalDate.now().equals(wake.toLocalDate());

            if(isCurrentDay){
                wake = wake.withTime(LocalTime.now());
            }

            int gap = Seconds.secondsBetween(wake,bed).getSeconds();
            int numTests = visit.getNumberOfTests(i);

            DateTime begin = wake;

            if(gap > 6*60*60) {
                gap -= 6*60*60;
                int period = gap;
                if(numTests>1){
                    period = gap / (numTests-1);
                }
                if (period <= 0) {
                    period = 10;
                }
                for (int j = 0; j < numTests; j++) {
                    if(!isCurrentDay || index>=state.currentTestSession) {
                        begin = begin.plusSeconds(random.nextInt(period));
                        testSessions.get(index).setScheduledTime(begin);
                        begin = begin.plusHours(2);
                    }
                    index++;
                }
            } else {
                int period = gap / (numTests+1);
                if (period <= 0) {
                    period = 10;
                }
                for (int j = 0; j < numTests; j++) {
                    if(!isCurrentDay || index>=state.currentTestSession) {
                        begin = begin.plusSeconds(period);
                        testSessions.get(index).setScheduledTime(begin);
                    }
                    index++;
                }

            }
            startDate = startDate.plusDays(1);
        }
    }


    protected void scheduleTestsForVisit(CircadianClock clock, ParticipantState state, Visit visit, boolean isRescheduling) {

    }

    // The following functions can feel a little misplaced.
    // Usually, any manipulation of the participant state should be left in the participant class.
    // I'd like to decrease any confusion as to when to use what class.
    // The participant class depends on member variables for the most part while this class is stateless.
    //
    // Minimizing the use of the study singleton makes it easier to unit test these classes at the moment.
    // ^ This is the driving force behind this decision.
    // As a compromise, this will generate an object that the participant can then consume.
    //
    // At some point, I would like to create a study class strictly for unit tests to help when these cases occur.
    // Any suggestions are welcome

    public ParticipantState getExistingParticipantState(ExistingData existingData) {

        ParticipantState state = Study.getParticipant().getState();

        DateTime startDate = JodaUtil.fromUtcDouble(existingData.first_test.session_date);
        Study.getScheduler().initializeVisits(startDate,Study.getParticipant());

        state.circadianClock = CircadianClock.fromWakeSleepSchedule(existingData.wake_sleep_schedule);

        List<TestScheduleSession> scheduleSessions = existingData.test_schedule.sessions;
        for(TestScheduleSession scheduleSession : scheduleSessions){

            Log.i(tag,"week = "+scheduleSession.week+", day = "+scheduleSession.day+" session = "+scheduleSession.session);
            // figure out what visit - test this
            int sessionId = Integer.valueOf(scheduleSession.session_id);
            int visitIndex = getVisitIndex(sessionId);
            int testIndex = getTestIndex(scheduleSession.week,scheduleSession.day,scheduleSession.session);

            TestSession testSession = state.visits.get(visitIndex).testSessions.get(testIndex);
            DateTime scheduledDateTime = JodaUtil.fromUtcDouble(scheduleSession.session_date);

            Log.i(tag,"visitIndex = "+visitIndex+", testIndex = "+testIndex+" - "+scheduledDateTime.toString());

            DateTime prescribedDateTime = testSession.getPrescribedTime();
            LocalTime scheduleTime = scheduledDateTime.toLocalTime();

            testSession.setPrescribedTime(prescribedDateTime.withTime(scheduleTime));
            testSession.setScheduledTime(scheduledDateTime);

            if(testSession.getExpirationTime().isBeforeNow()){
                testSession.markMissed();
            }

        }

        for (int i = 0; i < state.visits.size(); i++) {
            Visit visit = state.visits.get(i);
            int last = visit.testSessions.size()-1;
            visit.setActualStartDate(visit.testSessions.get(0).getScheduledTime());
            visit.setActualEndDate(visit.testSessions.get(last).getScheduledTime().plusDays(1));
        }

        SessionInfo latestSession = existingData.latest_test;
        state.currentVisit = getVisitIndex(latestSession.session);
        state.currentTestSession = getTestIndex(latestSession.week,latestSession.day,latestSession.session);
        state.currentTestSession++;

        if(isAtEndOfVisit(latestSession.week,state.currentTestSession)){
            state.currentVisit++;
            state.currentTestSession = 0;
        }

        state.hasValidSchedule = true;
        state.isStudyRunning = true;

        return state;
    }

    public int getVisitIndex(int sessionId){
        return 0;
    }

    public int getTestIndex(int week, int dayIndex, int dailyIndex){
        return 0;
    }

    public int getDayIndex(int visit, int test){
        return 0;
    }

    public boolean isAtEndOfVisit(int visit, int test){
        return false;
    }

}
