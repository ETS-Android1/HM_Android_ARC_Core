package com.healthymedium.arc.study;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.notifications.NotificationUtil;
import com.healthymedium.arc.paths.templates.LandingTemplate;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.questions.QuestionSignature;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class StateMachineAlpha extends StateMachine {

    public static final int PATH_SETUP_PARTICIPANT = 0;         //
    public static final int PATH_COMMITMENT = 9;
    public static final int PATH_COMMITMENT_REBUKED = 10;
    public static final int PATH_NOTIFICATIONS_OVERVIEW = 11;
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

        if(participant.hasRebukedCommitmentToStudy()){
            state.currentPath = PATH_COMMITMENT_REBUKED;
            return;
        }

        if(!participant.hasCommittedToStudy()){
            state.currentPath = PATH_COMMITMENT;
            return;
        }

        if(!participant.hasBeenShownNotificationOverview()){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(!participant.hasSchedule()){
            state.currentPath = PATH_SETUP_AVAILABILITY;
            return;
        }

        if(participant.getState().currentTestCycle == 0){
            Log.i("StateMachine", "init finished, setting lifecycle to baseline");
            state.lifecycle = LIFECYCLE_BASELINE;
        } else if(participant.getState().currentTestCycle == 4) {
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

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if (participant.getCurrentTestSession().isOngoing()) {
            Log.i("StateMachine", "loading in the middle of an indexed test, marking it abandoned");
            abandonTest();
            decidePath();
            return;
        }

        cache.segments.clear();

        if(participant.getCurrentTestCycle().getActualStartDate().isAfterNow()){
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
            loadTestDataFromCache();
            cache.data.clear();

            RestClient client = Study.getRestClient();
            client.submitTest(participant.getCurrentTestSession());
            participant.moveOnToNextTestSession(true);
            save();
            decidePath();
            return;
        }

        currentlyInTestPath = true;

        if (!participant.getCurrentTestCycle().hasThereBeenAFinishedTest()){
            Log.i("StateMachine", "setting path for first of baseline");
            state.currentPath = PATH_TEST_FIRST_OF_BASELINE;
            return;
        }

        if (!participant.getCurrentTestDay().hasThereBeenAFinishedTest()) {
            Log.i("StateMachine", "setting path for first of day");
            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }

        Log.i("StateMachine", "setting path for baseline test");
        state.currentPath = PATH_TEST_BASELINE;
    }

    private void decidePathArc(){
        Participant participant = Study.getInstance().getParticipant();

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(participant.getState().currentTestCycle == 4) {
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        TestCycle cycle = participant.getCurrentTestCycle();
        TestDay day = participant.getCurrentTestDay();

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

        if(cycle.getActualStartDate().isAfterNow()){
            Log.i("StateMachine", "indexed cycle hasn't started, setting lifecycle to idle");
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

            RestClient client = Study.getRestClient();
            client.submitTest(participant.getCurrentTestSession());
            participant.moveOnToNextTestSession(true);
            participant.save();
            decidePath();
            return;
        }

        currentlyInTestPath = true;

        if (!cycle.hasThereBeenAFinishedTest()){
            Log.i("StateMachine", "setting path for first of cycle");
            state.currentPath = PATH_TEST_FIRST_OF_VISIT;
            return;
        }

        if (!day.hasThereBeenAFinishedTest()) {
            Log.i("StateMachine", "setting path for first of day");
            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }

        Log.i("StateMachine", "setting path for test");
        state.currentPath = PATH_TEST_OTHER;
    }

    private void decidePathIdle() {
        TestCycle cycle = Study.getCurrentTestCycle();

        if(!NotificationUtil.areNotificationsEnabled(Application.getInstance())){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if (cycle.getActualStartDate().isBeforeNow()) {
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
            case LIFECYCLE_ARC:
                switch (state.currentPath){
                    case PATH_TEST_NONE:
                        break;
                    case PATH_NOTIFICATIONS_OVERVIEW:
                        break;
                    default:
                        Log.i(tag, "gather data from test");
                        // set up a loading dialog in case this takes a bit
                        LoadingDialog dialog = new LoadingDialog();
                        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

                        TestSession currentTest = Study.getCurrentTestSession();
                        currentTest.markCompleted();
                        if(Study.getCurrentTestDay().getNumberOfTestsAvailableNow()==0){
                            setTestCompleteFlag(true);
                        }
                        loadTestDataFromCache();

                        RestClient client = Study.getRestClient();
                        client.submitTest(Study.getCurrentTestSession());
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

    // state machine helpers ---------------------------------------------------------------------

    public void addWelcome() {
//        List<BaseFragment> fragments = new ArrayList<>();
//
//        if(Config.IS_REMOTE) {
//            // I commit or I'm not able to commit
//            fragments.add(new QuestionRemoteStudyCommitment(
//                    true,
//                    ViewUtil.getString(R.string.testing_commitment),
//                    ViewUtil.getString(R.string.onboarding_body),
//                    ViewUtil.getString(R.string.radio_commit),
//                    ViewUtil.getString(R.string.radio_nocommit)
//            ));
//
//        } else {
//            // I understand
//            fragments.add(new QuestionSingleButton(
//                    false,
//                    ViewUtil.getString(R.string.onboarding_header),
//                    ViewUtil.getString(R.string.onboarding_body),
//                    ViewUtil.getString(R.string.button_continue),
//                    ViewUtil.getString(R.string.radio_understand)));
//        }
//
//        PathSegment segment = new PathSegment(fragments);
//        enableTransition(segment,true);
//        cache.segments.add(segment);
    }

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
        List<BaseFragment> fragments = new ArrayList<>();

        // Default
        fragments.add(new LandingTemplate());
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void checkForSignaturePage(boolean allowHelp){
        if(Config.ENABLE_SIGNATURES) {
            addSignaturePage(allowHelp);
        }
    }

    public void addSignaturePage(boolean allowHelp){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new QuestionSignature(
                false,
                allowHelp,
                ViewUtil.getString(R.string.idverify_header),
                ViewUtil.getString(R.string.testing_id_header)));
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    // --------------------------------------------------------------------------

    public void setPathFirstOfBaseline(){
        addTestLandingPage();
        checkForSignaturePage(true);
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
        Study.getCurrentTestSession().markStarted();
    }

    public void setPathBaselineTest(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }


    public void setPathNoTests(){
        //  leave empty for now
    }

    public void setPathTestFirstOfVisit(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathTestFirstOfDay(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathTestOther(){
        checkForLandingPage();
        checkForSignaturePage(true);
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
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
            case PATH_COMMITMENT:
                return "PATH_COMMITMENT";
            case PATH_COMMITMENT_REBUKED:
                return "PATH_COMMITMENT_REBUKED";
            case PATH_NOTIFICATIONS_OVERVIEW:
                return "PATH_NOTIFICATIONS_OVERVIEW";
            default:
                return "INVALID";
        }
    }

    @Override
    public void loadTestDataFromCache() {
        loadCognitiveTestFromCache();
    }
}
