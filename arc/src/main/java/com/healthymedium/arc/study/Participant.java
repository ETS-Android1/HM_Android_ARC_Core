package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

public class Participant {

    public static final String TAG_PARTICIPANT_STATE = "ParticipantState";

    protected ParticipantState state;

    public void initialize(){
        state = new ParticipantState();
    }

    public void load() {
        load(false);
    }

    public void load(boolean overwrite){
        if(state!=null && !overwrite){
            return;
        }
        state = PreferencesManager.getInstance().getObject(TAG_PARTICIPANT_STATE,ParticipantState.class);
    }

    public void save(){
        PreferencesManager.getInstance().putObject(TAG_PARTICIPANT_STATE, state);
    }

    public boolean hasId(){
        return state.id!=null;
    }

    public String getId(){
        return state.id;
    }

    public boolean hasSchedule(){
        return state.hasValidSchedule;
    }

    public void markPaused(){
        state.lastPauseTime = DateTime.now();
    }

    public void markResumed(){

        /*
        We're checking three situations here:
        - Are we currently in a test?
            If so check and see if we should abandon it
        - Should we be in a test, but the state machine is not set to a test path?
            If so let's skip to the next segment
        - Else are we currently in a test path?
            If so, have the state machine decide where to go next
         */
        if(isCurrentlyInTestSession()){
            if(checkForTestAbandonment())
            {
                Study.getInstance().abandonTest();
            }
        } else if(shouldCurrentlyBeInTestSession() && !Study.getStateMachine().isCurrentlyInTestPath()){
            Study.skipToNextSegment();
        }
        else if(Study.getStateMachine().isCurrentlyInTestPath()){
            Study.getStateMachine().decidePath();
            Study.getStateMachine().setupPath();
            Study.getStateMachine().openNext();
        }
    }

    public boolean checkForTestAbandonment(){
        return state.lastPauseTime.plusMinutes(5).isBeforeNow();
    }
    public boolean isCurrentlyInTestSession(){
        if(state.visits.size()==0){
            return false;
        }

        return getCurrentTestSession().isOngoing();
    }

    public boolean shouldCurrentlyBeInTestSession(){
        if(state.visits.size()==0){
            return false;
        }
        return getCurrentTestSession().getScheduledTime().isBeforeNow();
    }

    public Visit getCurrentVisit(){
        if(state.visits.size()>0) {
            return state.visits.get(state.currentVisit);
        }
        return null;
    }

    public void moveOnToNextTestSession(boolean scheduleNotifications){
        Log.i("Participant", "moveOnToNextTestSession");
        state.currentTestSession++;
        if(state.currentTestSession>=state.visits.get(state.currentVisit).testSessions.size()){
            Proctor.stopService(Application.getInstance());
            state.currentTestSession = 0;
            state.currentVisit++;
            if(state.currentVisit>=state.visits.size()){
                state.isStudyRunning = false;
            } else if(scheduleNotifications){
                Study.getScheduler().scheduleNotifications(getCurrentVisit(), false);
            }
        }
        save();
    }

    public TestSession getCurrentTestSession(){
        return getCurrentVisit().getTestSessions().get(state.currentTestSession);
    }

    public void setCircadianClock(CircadianClock clock){
        state.circadianClock = clock;
    }

    public CircadianClock getCircadianClock(){
        return state.circadianClock;
    }

    public boolean isStudyRunning(){
        return state.isStudyRunning;
    }

    public void markStudyStarted(){
        state.isStudyRunning = true;
        save();
    }

    public void markStudyStopped(){
        state.isStudyRunning = false;
        save();
    }

    public ParticipantState getState(){
        return state;
    }

    public void setState(ParticipantState state){
        this.state = state;
        save();
    }

}
