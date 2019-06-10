package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.api.tests.CognitiveTest;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.utilities.NavigationManager;

public class StudyStateMachineAlpha extends StudyStateMachine {

    public static final int PATH_SETUP_PARTICIPANT = 0;         //
    public static final int PATH_SETUP_AVAILABILITY = 1;        //

    public static final int PATH_TEST_FIRST_OF_BASELINE = 2;    // first test of the baseline
    public static final int PATH_TEST_BASELINE = 3;             // every other test in the baseline

    public static final int PATH_TEST_NONE = 4;                 // no tests available
    public static final int PATH_TEST_FIRST_OF_VISIT = 5;       // first test of a visit, this trumps the first day path
    public static final int PATH_TEST_FIRST_OF_DAY = 6;         // first test on a given day
    public static final int PATH_TEST_OTHER = 7;                // every test that doesn't listed above

    public static final int PATH_STUDY_OVER = 8;                //

    public static final int LIFECYCLE_INIT = 0;                 //
    public static final int LIFECYCLE_BASELINE = 1;             //
    public static final int LIFECYCLE_IDLE = 3;                 //
    public static final int LIFECYCLE_ARC = 4;                  //
    public static final int LIFECYCLE_OVER = 5;                 //


    @Override
    public void initialize() {
        super.initialize();
        state.lifecycle = LIFECYCLE_INIT;
        state.currentPath = PATH_SETUP_PARTICIPANT;
    }

    @Override
    public boolean isIdle() {
        return state.lifecycle==LIFECYCLE_IDLE;
    }

    // deciding paths ------------------------------------------------------------------------------

    @Override
    public void decidePath(){
        Log.i("StateMachine", "decidePath");
        Log.i("StateMachine", "lifecycle = "+getLifecycleName(state.lifecycle));

        switch (state.lifecycle) {
            case LIFECYCLE_INIT:
                decidePathInit();
                break;
            case LIFECYCLE_BASELINE:
                decidePathBaseline();
                break;
            case LIFECYCLE_ARC:
                decidePathArc();
                break;
            case LIFECYCLE_IDLE:
                decidePathIdle();
                break;
            case LIFECYCLE_OVER:
                decidePathOver();
                break;
        }
    }

    private void decidePathInit(){
        cache.segments.clear();
        Participant participant = Study.getParticipant();

        if(!participant.hasId()){
            state.currentPath = PATH_SETUP_PARTICIPANT;
            return;
        }

        if(!participant.hasSchedule()){
            state.currentPath = PATH_SETUP_AVAILABILITY;
            return;
        }

        if(participant.getState().currentVisit==0){
            Log.i("StateMachine", "init finished, setting lifecycle to baseline");
            state.lifecycle = LIFECYCLE_BASELINE;
        } else if(participant.getState().currentVisit==4) {
            Log.i("StateMachine", "init finished, setting lifecycle to over");
            state.lifecycle = LIFECYCLE_OVER;
        } else {
            Log.i("StateMachine", "init finished, setting lifecycle to idle");
            state.lifecycle = LIFECYCLE_IDLE;

        }
        decidePath();
    }

    private void decidePathBaseline(){
        Participant participant = Study.getInstance().getParticipant();

        if (participant.getCurrentTestSession().isOngoing()) {
            Log.i("StateMachine", "loading in the middle of an indexed test, marking it abandoned");
            abandonTest();
            decidePath();
            return;
        }

        cache.segments.clear();

        if(participant.getCurrentVisit().getActualStartDate().isAfterNow()){
            Log.i("StateMachine", "indexed visit hasn't started, setting lifecycle to idle");
            state.lifecycle = LIFECYCLE_IDLE;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().getScheduledTime().minusMinutes(5).isAfterNow()) {
            Log.i("StateMachine", "indexed test hasn't started, do nothing");
            state.currentPath = PATH_TEST_NONE;
            return;
        }

        if (participant.getCurrentTestSession().getExpirationTime().isBeforeNow()) {
            Log.i("StateMachine", "indexed test has expired, marking it as such");
            participant.getCurrentTestSession().markMissed();
            loadCognitiveTestFromCache();
            cache.data.clear();

            Study.getRestClient().submitTest(participant.getCurrentTestSession());
            participant.moveOnToNextTestSession(true);
            save();
            decidePath();
            return;
        }

        currentlyInTestPath = true;

        if (participant.getState().currentTestSession == 0) {
            Log.i("StateMachine", "setting path for first of baseline");
            state.currentPath = PATH_TEST_FIRST_OF_BASELINE;
            return;
        }

        if (!participant.getCurrentVisit().hasThereBeenAFinishedTest(participant.getCurrentTestSession().getDayIndex())) {
            Log.i("StateMachine", "setting path for first of day");
            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }

        Log.i("StateMachine", "setting path for baseline test");
        state.currentPath = PATH_TEST_BASELINE;
    }

    private void decidePathArc(){
        Participant participant = Study.getInstance().getParticipant();

        if(participant.getState().currentVisit == 4) {
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        Visit visit = participant.getCurrentVisit();

        if(!participant.isStudyRunning()){
            Log.i("StateMachine", "study isn't running, setting lifecycle to over");
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().isOngoing()) {
            abandonTest();
            decidePath();
            return;
        }

        cache.segments.clear();

        if(visit.getActualStartDate().isAfterNow()){
            Log.i("StateMachine", "indexed visit hasn't started, setting lifecycle to idle");
            state.lifecycle = LIFECYCLE_IDLE;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().getScheduledTime().minusMinutes(5).isAfterNow()) {
            Log.i("StateMachine", "indexed test hasn't started, do nothing");
            state.currentPath = PATH_TEST_NONE;
            return;
        }

        if (participant.getCurrentTestSession().getExpirationTime().isBeforeNow()) {
            Log.i("StateMachine", "indexed test has expired, marking it as such");
            participant.getCurrentTestSession().markMissed();
            Study.getRestClient().submitTest(participant.getCurrentTestSession());
            participant.moveOnToNextTestSession(true);
            participant.save();
            decidePath();
            return;
        }

        currentlyInTestPath = true;

        if (!visit.hasThereBeenAFinishedTest()){
            Log.i("StateMachine", "setting path for first of visit");
            state.currentPath = PATH_TEST_FIRST_OF_VISIT;
            return;
        }

        if (!visit.hasThereBeenAFinishedTest(participant.getCurrentTestSession().getDayIndex())) {
            Log.i("StateMachine", "setting path for first of day");
            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }

        Log.i("StateMachine", "setting path for test");
        state.currentPath = PATH_TEST_OTHER;
    }

    private void decidePathIdle() {
        Visit visit = Study.getCurrentVisit();

        if (visit.getActualStartDate().isBeforeNow()) {
            state.lifecycle = LIFECYCLE_ARC;
            decidePath();
        } else {
            state.currentPath = PATH_TEST_NONE;
        }
    }

    private void decidePathOver() {
        state.currentPath = PATH_STUDY_OVER;
    }

    // setting up paths ----------------------------------------------------------------------------

//    @Override
//    protected void setupPath(){
//
//    }

    @Override
    protected void endOfPath(){
        Log.i("StateMachine", "endOfPath");
        Log.i("StateMachine", "lifecycle = "+getLifecycleName(state.lifecycle));
        Log.i("StateMachine", "path = "+getPathName(state.currentPath));

        switch (state.lifecycle) {
            case LIFECYCLE_INIT:
                switch (state.currentPath){
                    case PATH_SETUP_PARTICIPANT:
                        break;
                    case PATH_SETUP_AVAILABILITY:
                        break;
                }
                break;
            case LIFECYCLE_BASELINE:
                switch (state.currentPath){
                    case PATH_TEST_NONE:
                        break;
                    default:
                        Log.i("StateMachine", "gather data from test");
                        // set up a loading dialog in case this takes a bit
                        LoadingDialog dialog = new LoadingDialog();
                        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

                        Study.getCurrentTestSession().markCompleted(true);
                        if(Study.getCurrentVisit().getNumberOfTestsAvailable()==0){
                            setTestCompleteFlag(true);
                        }
                        loadCognitiveTestFromCache();
                        Study.getRestClient().submitTest(Study.getCurrentTestSession());
                        Study.getParticipant().moveOnToNextTestSession(true);
                        save();

                        dialog.dismiss();
                        break;
                }
                break;
            case LIFECYCLE_ARC:
                switch (state.currentPath){
                    case PATH_TEST_NONE:
                        break;
                    default:
                        Log.i("StateMachine", "gather data from test");
                        // set up a loading dialog in case this takes a bit
                        LoadingDialog dialog = new LoadingDialog();
                        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

                        Study.getCurrentTestSession().markCompleted(true);
                        if(Study.getCurrentVisit().getNumberOfTestsAvailable()==0){
                            setTestCompleteFlag(true);
                        }
                        loadCognitiveTestFromCache();
                        Study.getRestClient().submitTest(Study.getCurrentTestSession());
                        Study.getParticipant().moveOnToNextTestSession(true);
                        save();

                        dialog.dismiss();
                        break;
                }
                break;
            case LIFECYCLE_IDLE:
                break;
            case LIFECYCLE_OVER:
                break;
        }
        currentlyInTestPath = false;
    }

    // ---------------------------------------------------------------------------------------------

    public void loadCognitiveTestFromCache(){
        Log.i("StateMachine", "loadCognitiveTestFromCache");
        CognitiveTest cognitiveTest = new CognitiveTest();
        cognitiveTest.load(cache.data);
        Study.getCurrentTestSession().addTestData(cognitiveTest);
    }

    // state machine helpers ---------------------------------------------------------------------

    public void checkForLandingPage(){
        if(Config.OPENED_FROM_NOTIFICATION) {
            Config.OPENED_FROM_NOTIFICATION = false;

            // In a visit
            // Try to start a test
            Study.getCurrentTestSession().markStarted();
        } else {
            addTestLandingPage();
        }
    }

    public void addTestLandingPage(){
        //  leave empty for now
    }


    // --------------------------------------------------------------------------

    public void setPathFirstOfBaseline(){
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        Study.getCurrentTestSession().markStarted();
    }

    public void setPathBaselineTest(){
        checkForLandingPage();
        addContextSurvey();
        addTests();
        addInterruptedPage();
    }


    public void setPathNoTests(){
        //  leave empty for now
    }

    public void setPathTestFirstOfVisit(){
        checkForLandingPage();
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
    }

    public void setPathTestFirstOfDay(){
        checkForLandingPage();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
    }

    public void setPathTestOther(){
        checkForLandingPage();
        addContextSurvey();
        addTests();
        addInterruptedPage();
    }

    public void setPathOver(){
        setPathNoTests();
    }


    // utility functions ---------------------------------------------------------------------------

    @Override
    public String getLifecycleName(int lifecycle){
        switch (lifecycle){
            case LIFECYCLE_INIT:
                return "INIT";
            case LIFECYCLE_BASELINE:
                return "BASELINE";
            case LIFECYCLE_IDLE:
                return "IDLE";
            case LIFECYCLE_ARC:
                return "ARC";
            case LIFECYCLE_OVER:
                return "OVER";
            default:
                return "INVALID";
        }
    }

    @Override
    public String getPathName(int path){
        switch (path){
            case PATH_SETUP_PARTICIPANT:
                return "SETUP_PARTICIPANT";
            case PATH_SETUP_AVAILABILITY:
                return "SETUP_AVAILABILITY";
            case PATH_TEST_FIRST_OF_BASELINE:
                return "TEST_FIRST_OF_BASELINE";
            case PATH_TEST_BASELINE:
                return "TEST_BASELINE";
            case PATH_TEST_NONE:
                return "TEST_NONE";
            case PATH_TEST_FIRST_OF_VISIT:
                return "TEST_FIRST_OF_VISIT";
            case PATH_TEST_FIRST_OF_DAY:
                return "TEST_FIRST_OF_DAY";
            case PATH_TEST_OTHER:
                return "TEST_OTHER";
            case PATH_STUDY_OVER:
                return "STUDY_OVER";
            default:
                return "INVALID";
        }
    }

    @Override
    public void loadTestDataFromCache() {
        loadCognitiveTestFromCache();
    }
}
