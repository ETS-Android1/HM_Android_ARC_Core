package com.healthymedium.arc.study;

import android.content.res.Resources;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.paths.availability.AvailabilityBed;
import com.healthymedium.arc.paths.availability.AvailabilityConfirm;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.paths.templates.TestInfoTemplate;
import com.healthymedium.arc.paths.tests.TestProgress;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.core.SimplePopupScreen;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.path_data.AvailabilityPathData;
import com.healthymedium.arc.path_data.ChronotypePathData;
import com.healthymedium.arc.path_data.ContextPathData;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.path_data.PriceTestPathData;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.path_data.SymbolsTestPathData;
import com.healthymedium.arc.path_data.WakePathData;
import com.healthymedium.arc.paths.availability.AvailabilityWake;
import com.healthymedium.arc.paths.informative.ScheduleCalendar;
import com.healthymedium.arc.paths.questions.QuestionAdjustSchedule;
import com.healthymedium.arc.paths.setup.SetupAuthCode;
import com.healthymedium.arc.paths.templates.InfoTemplate;
import com.healthymedium.arc.paths.questions.QuestionCheckBoxes;
import com.healthymedium.arc.paths.questions.QuestionDuration;
import com.healthymedium.arc.paths.questions.QuestionInteger;
import com.healthymedium.arc.paths.questions.QuestionPolar;
import com.healthymedium.arc.paths.questions.QuestionRadioButtons;
import com.healthymedium.arc.paths.questions.QuestionRating;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.paths.setup.SetupParticipant;
import com.healthymedium.arc.paths.setup.SetupParticipantConfirm;
import com.healthymedium.arc.paths.setup.SetupWelcome;
import com.healthymedium.arc.paths.tests.GridLetters;
import com.healthymedium.arc.paths.tests.GridStudy;
import com.healthymedium.arc.paths.tests.GridTest;
import com.healthymedium.arc.paths.tests.PriceTestCompareFragment;
import com.healthymedium.arc.paths.tests.PriceTestMatchFragment;
import com.healthymedium.arc.paths.tests.QuestionInterrupted;
import com.healthymedium.arc.paths.tests.SymbolTest;
import com.healthymedium.arc.paths.tests.TestBegin;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.PriceManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StateMachine {

    public static final String TAG_STUDY_STATE_CACHE = "StudyStateCache";
    public static final String TAG_STUDY_STATE = "StudyState";
    public static final String TAG_TEST_COMPLETE = "TestCompleteFlag";

    protected String tag = getClass().getSimpleName();

    protected State state;
    protected StateCache cache;
    protected boolean currentlyInTestPath = false;

    public StateMachine() {

    }

    protected void enableTransition(PathSegment segment, boolean animateEntry){
        int size = segment.fragments.size();
        if(size==0){
            return;
        }

        segment.fragments.get(0).setTransitionSet(TransitionSet.getSlidingDefault(animateEntry));
        for(int i=1;i<size;i++){
            segment.fragments.get(i).setTransitionSet(TransitionSet.getSlidingDefault());
        }
    }

    protected void enableTransitionGrids(PathSegment segment, boolean animateEntry){
        if (animateEntry) {
            segment.fragments.get(1).setTransitionSet(TransitionSet.getSlidingDefault());
            segment.fragments.get(2).setTransitionSet(TransitionSet.getSlidingDefault());
            segment.fragments.get(3).setTransitionSet(TransitionSet.getSlidingDefault());
        }
    }

    public void initialize(){
        state = new State();
        cache = new StateCache();
    }

    public void load(){
        load(false);
    }

    public void load(boolean overwrite){
        Log.d(tag,"load(overwrite = "+overwrite+")");
        if(state!=null && !overwrite){
            return;
        }
        state = PreferencesManager.getInstance().getObject(TAG_STUDY_STATE, State.class);
        cache = CacheManager.getInstance().getObject(TAG_STUDY_STATE_CACHE, StateCache.class);
    }

    public void save(){
        save(false);
    }

    public void save(boolean saveCache){
        Log.d(tag,"save(saveCache = "+saveCache+")");
        PreferencesManager.getInstance().putObject(TAG_STUDY_STATE, state);
        CacheManager.getInstance().putObject(TAG_STUDY_STATE_CACHE,cache);
        if(saveCache){
            CacheManager.getInstance().save(TAG_STUDY_STATE_CACHE);
        }
    }

    public void decidePath(){

    }

    public void abandonTest(){
        Participant participant = Study.getParticipant();

        Log.i(tag, "loading in the middle of an indexed test, marking it abandoned");
        participant.getCurrentTestSession().markAbandoned();

        Log.i(tag, "collecting data from each existing segment");
        for(PathSegment segment : cache.segments){
            BaseData object = segment.collectData();
            if(object!=null){
                cache.data.add(object);
            }
        }

        loadTestDataFromCache();
        cache.segments.clear();
        cache.data.clear();

        Study.getRestClient().submitTest(participant.getCurrentTestSession());
        participant.moveOnToNextTestSession(true);
        save();
    }

    protected void setupPath(){

    }

    // this is where we can use the cache of segments
    protected void endOfPath(){

    }

    public boolean skipToNextSegment(){

        if(cache.segments.size() > 0) {
            BaseData object = cache.segments.get(0).collectData();
            if (object != null) {
                cache.data.add(object);
            }
            cache.segments.remove(0);

            NavigationManager.getInstance().clearBackStack();
            Study.getInstance().getParticipant().save();
            save();
        }

        if(cache.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            cache.data.clear();
            decidePath();
            setupPath();
            return openNext();
        }
    }

    public boolean openNext() {
        return openNext(0);
    }

    public boolean openNext(int skips){
        save();
        if(cache.segments.size()>0){
            if(cache.segments.get(0).openNext(skips)) {
                return true;
            } else {
                return endOfSegment();
            }
        } else {
            return moveOn();
        }
    }

    protected boolean endOfSegment(){
        // else at the end of segment
        BaseData object = cache.segments.get(0).collectData();
        if(object!=null){
            cache.data.add(object);
        }
        cache.segments.remove(0);

        NavigationManager.getInstance().clearBackStack();
        Study.getInstance().getParticipant().save();
        save();

        if(cache.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            return moveOn();
        }
    }

    protected boolean moveOn(){
        cache.data.clear();
        decidePath();
        setupPath();
        Study.getInstance().getParticipant().save();
        save();
        return openNext();
    }

    public boolean openPrevious() {
        return openPrevious(0);
    }

    public boolean openPrevious(int skips){
        if(cache.segments.size()>0){
            return cache.segments.get(0).openPrevious(skips);
        }
        return false;
    }

    // ------------------------------------------


    protected void setTestCompleteFlag(boolean complete){
        Log.i(tag, "setTestCompleteFlag("+complete+")");
        PreferencesManager.getInstance().putBoolean(TAG_TEST_COMPLETE,complete);
    }

    protected boolean isTestCompleteFlagSet(){
        return PreferencesManager.getInstance().getBoolean(TAG_TEST_COMPLETE,false);
    }

    public boolean isCurrentlyInTestPath(){
        return currentlyInTestPath;
    }

    public boolean isIdle(){
        return false;
    }


    public boolean hasValidFragments() {
        if(cache.segments.size() == 0) {
            return false;
        }

        for(int i = 0; i < cache.segments.size(); i++) {
            if(cache.segments.get(i).fragments.size() == 0) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public void setPathSetupParticipant(int firstDigitCount, int secondDigitCount, int authDigitCount){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new SetupWelcome());
        fragments.add(new SetupParticipant(firstDigitCount,secondDigitCount));
        fragments.add(new SetupParticipantConfirm(false,firstDigitCount,secondDigitCount));

        if (Config.EXPECTS_2FA_TEXT) {
            fragments.add(new SetupAuthCode(true, true, authDigitCount, ViewUtil.getString(R.string.login_enter_2FA)));
        }
        else {
            fragments.add(new SetupAuthCode(true, false, authDigitCount, ViewUtil.getString(R.string.login_enter_raterID)));
        }

        PathSegment segment = new PathSegment(fragments,SetupPathData.class);
        enableTransition(segment,false);
        cache.segments.add(segment);
    }

    // default
    public void setPathSetupParticipant(){
        setPathSetupParticipant(5,3,5);
    }

    // ---------------------------------------------------------------------------------------------

    public void setPathSetupAvailability(int minWakeTime, int maxWakeTime, boolean reschedule){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.setup_avail_header),
                null,
                res.getString(R.string.availability_body),
                res.getString(R.string.button_begin)));

        fragments.add(new AvailabilityWake());
        fragments.add(new AvailabilityBed(minWakeTime,maxWakeTime));
        fragments.add(new AvailabilityConfirm(minWakeTime, maxWakeTime, reschedule, true));

        PathSegment segment = new PathSegment(fragments,AvailabilityPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    // default
    public void setPathSetupAvailability(){
        setPathSetupAvailability(4,24,false);
    }

    // ---------------------------------------------------------------------------------------------

    public void addChronotypeSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.chronotype_header),
                res.getString(R.string.chronotype_subhead),
                res.getString(R.string.chronotype_0_body),
                res.getString(R.string.button_begin)));

        fragments.add(new QuestionPolar(true, res.getString(R.string.chronotype_1_q1),""));

        List<String> workingDayCountOptions = new ArrayList<>();
        workingDayCountOptions.add("0");
        workingDayCountOptions.add("1");
        workingDayCountOptions.add("2");
        workingDayCountOptions.add("3");
        workingDayCountOptions.add("4");
        workingDayCountOptions.add("5");
        workingDayCountOptions.add("6");
        workingDayCountOptions.add("7");

        fragments.add(new QuestionRadioButtons(true,false, res.getString(R.string.chronotype_1_q2), res.getString(R.string.list_selectone ),workingDayCountOptions));

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.chronotype_header),
                "",
                res.getString(R.string.chronotype_2_body),
                res.getString(R.string.button_next)));

        CircadianClock clock;
        String weekday;
        LocalTime wakeTime = null;
        LocalTime bedTime = null;

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", java.util.Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekday = dayFormat.format(calendar.getTime());

        clock = Study.getParticipant().getCircadianClock();

        // Get previously entered wake time for today
        if(wakeTime==null && !clock.hasWakeRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            wakeTime = clock.getRhythm(index).getWakeTime();
        } else if(wakeTime==null){
            wakeTime = clock.getRhythm(weekday).getWakeTime();
        }

        // Get previously entered bed time for today
        if(bedTime==null && !clock.hasBedRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            bedTime = clock.getRhythm(index).getBedTime();
        } else if(bedTime==null){
            bedTime = clock.getRhythm(weekday).getBedTime();
        }

        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_work_days_sleep), res.getString(R.string.chronotype_body_sleep), bedTime));
        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_work_days_wake), res.getString(R.string.chronotype_body_wake), wakeTime));
        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_workfree_sleep), res.getString(R.string.chronotype_body_sleep), bedTime));
        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_workfree_wake), res.getString(R.string.chronotype_body_wake), wakeTime));

        PathSegment segment = new PathSegment(fragments,ChronotypePathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addWakeSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.sleepwakesurvey_header),
                res.getString(R.string.sleepwakesurvey_subhead),
                res.getString(R.string.sleepwakesurvey_body),
                res.getString(R.string.button_begin)));

        CircadianClock clock;
        String weekday;
        LocalTime wakeTime = null;
        LocalTime bedTime = null;

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", java.util.Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekday = dayFormat.format(calendar.getTime());

        clock = Study.getParticipant().getCircadianClock();

        // Get previously entered wake time for today
        if(wakeTime==null && !clock.hasWakeRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            wakeTime = clock.getRhythm(index).getWakeTime();
        } else if(wakeTime==null){
            wakeTime = clock.getRhythm(weekday).getWakeTime();
        }

        // Get previously entered bed time for today
        if(bedTime==null && !clock.hasBedRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            bedTime = clock.getRhythm(index).getBedTime();
        } else if(bedTime==null){
            bedTime = clock.getRhythm(weekday).getBedTime();
        }

        fragments.add(new QuestionTime(true, res.getString(R.string.wake_0_q1),"",bedTime));
        fragments.add(new QuestionDuration(true, res.getString(R.string.wake_0_q2)," "));
        fragments.add(new QuestionInteger(true, res.getString(R.string.wake_0_q3a), res.getString(R.string.wake_0_q3b),2));
        fragments.add(new QuestionTime(true, res.getString(R.string.wake_1_q1)," ",wakeTime));
        fragments.add(new QuestionTime(true, res.getString(R.string.wake_1_q2)," ",wakeTime));
        fragments.add(new QuestionRating(true, res.getString(R.string.wake_1_q3), res.getString(R.string.wake_drag), res.getString(R.string.wake_poor), res.getString(R.string.wake_excellent)));

        PathSegment segment = new PathSegment(fragments,WakePathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addContextSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.context_header),
                res.getString(R.string.context_subhead),
                res.getString(R.string.context_body),
                res.getString(R.string.button_begin)));

        List<String> who = new ArrayList<>();
        who.add(res.getString(R.string.context_q1_answers1));
        who.add(res.getString(R.string.context_q1_answers2));
        who.add(res.getString(R.string.context_q1_answers3));
        who.add(res.getString(R.string.context_q1_answers4));
        who.add(res.getString(R.string.context_q1_answers5));
        who.add(res.getString(R.string.context_q1_answers6));
        who.add(res.getString(R.string.context_q1_answers7));
        fragments.add(new QuestionCheckBoxes(true, res.getString(R.string.context_q1), res.getString(R.string.Context_q1_sub), who, res.getString(R.string.context_q1_answers1)));

        List<String> where = new ArrayList<>();
        where.add(res.getString(R.string.context_q2_answers1));
        where.add(res.getString(R.string.context_q2_answers2));
        where.add(res.getString(R.string.context_q2_answers3));
        where.add(res.getString(R.string.context_q2_answers4));
        where.add(res.getString(R.string.context_q2_answers5));
        where.add(res.getString(R.string.context_q2_answers6));
        where.add(res.getString(R.string.context_q2_answers7));
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q2), res.getString(R.string.Context_q2_sub), where));

        fragments.add(new QuestionRating(true, res.getString(R.string.context_q3), "", res.getString(R.string.context_bad), res.getString(R.string.context_good)));
        fragments.add(new QuestionRating(true, res.getString(R.string.context_q4), "", res.getString(R.string.context_tired), res.getString(R.string.context_active)));

        List<String> what = new ArrayList<>();
        what.add(res.getString(R.string.context_q5_answers1));
        what.add(res.getString(R.string.context_q5_answers2));
        what.add(res.getString(R.string.context_q5_answers3));
        what.add(res.getString(R.string.context_q5_answers4));
        what.add(res.getString(R.string.context_q5_answers5));
        what.add(res.getString(R.string.context_q5_answers6));
        what.add(res.getString(R.string.context_q5_answers7));
        what.add(res.getString(R.string.context_q5_answers8));
        what.add(res.getString(R.string.context_q5_answers9));
        what.add(res.getString(R.string.context_q5_answers10));
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q5), "", what));

        PathSegment segment = new PathSegment(fragments,ContextPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addTests(){

        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        StateInfoTemplate info = new StateInfoTemplate(
                false,
                res.getString(R.string.testing_intro_header),
                res.getString(R.string.testing_intro_subhead),
                res.getString(R.string.testing_intro_body),
                res.getString(R.string.button_next));
        //info.setEnterTransitions(R.anim.slide_in_right,R.anim.slide_in_left);
        fragments.add(info);
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);

        Integer[] orderArray = new Integer[]{1,2,3};
        List<Integer> order = Arrays.asList(orderArray);
        Collections.shuffle(order);
        for(int i =0;i<3;i++){
            switch(order.get(i)){
                case 1:
                    addSymbolsTest(i);
                    break;
                case 2:
                    addPricesTest(i);
                    break;
                case 3:
                    addGridTest(i);
                    break;
            }
        }
    }

    public void addPricesTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String testNumber = ViewUtil.getString(R.string.testing_header_one);
        testNumber = testNumber.replace("{Value1}", String.valueOf(index+1));
        testNumber = testNumber.replace("{Value2}", "3");

        TestInfoTemplate info = new TestInfoTemplate(
                testNumber,
                res.getString(R.string.price_header),
                res.getString(R.string.prices_body),
                "prices",
                res.getString(R.string.button_begin));
        fragments.add(info);

        fragments.add(new TestBegin());

        int size = PriceManager.getInstance().getPriceSet().size();
        for(int i=0;i<size;i++){
            fragments.add(new PriceTestCompareFragment(i));
        }

        fragments.add(new SimplePopupScreen(
                res.getString(R.string.price_overlay),
                res.getString(R.string.button_begin),
                3000,
                15000,
                true));

        fragments.add(new PriceTestMatchFragment());
        fragments.add(new TestProgress(ViewUtil.getString(R.string.prices_complete), index));
        PathSegment segment = new PathSegment(fragments,PriceTestPathData.class);
        cache.segments.add(segment);
    }

    public void addSymbolsTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String testNumber = ViewUtil.getString(R.string.testing_header_one);
        testNumber = testNumber.replace("{Value1}", String.valueOf(index+1));
        testNumber = testNumber.replace("{Value2}", "3");

        TestInfoTemplate info = new TestInfoTemplate(
                testNumber,
                res.getString(R.string.symbols_header),
                res.getString(R.string.symbols_body),
                "symbols",
                res.getString(R.string.button_begin));
        fragments.add(info);

        fragments.add(new TestBegin());

        fragments.add(new SymbolTest());
        fragments.add(new TestProgress(ViewUtil.getString(R.string.symbols_complete), index));
        PathSegment segment = new PathSegment(fragments,SymbolsTestPathData.class);
        cache.segments.add(segment);
    }

    public void addGridTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String testNumber = ViewUtil.getString(R.string.testing_header_one);
        testNumber = testNumber.replace("{Value1}", String.valueOf(index+1));
        testNumber = testNumber.replace("{Value2}", "3");

        TestInfoTemplate info0 = new TestInfoTemplate(
                testNumber,
                res.getString(R.string.grid_header),
                res.getString(R.string.grid_body),
                "grids",
                res.getString(R.string.button_next));
        fragments.add(info0);

        fragments.add(new TestBegin());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        fragments.add(new GridTest());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        GridTest gridTestFragment = new GridTest();
        gridTestFragment.second = true;
        fragments.add(gridTestFragment);
        fragments.add(new TestProgress(ViewUtil.getString(R.string.grids_complete), index));
        PathSegment segment = new PathSegment(fragments,GridTestPathData.class);
        enableTransitionGrids(segment,true);
        cache.segments.add(segment);
    }

    public void addInterruptedPage(){

        Resources res = Application.getInstance().getResources();

        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new QuestionInterrupted(false, res.getString(R.string.interrupted_body),""));
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void addFinishedPage(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String header;
        String subheader;
        String body;

        // Default
        header = res.getString(R.string.thank_you_header1);
        subheader = res.getString(R.string.thankyou_testcomplete_subhead1);
        body = res.getString(R.string.thankyou_testcomplete_body1);

        // Finished with study
        if(!Study.getParticipant().isStudyRunning()){
            //at the end of the line
            header = res.getString(R.string.thankyou_header3);
            subheader = res.getString(R.string.thankyou_finished_subhead3);
            body = res.getString(R.string.thankyou_body3);
        }
        else {
            ParticipantState participantState = Study.getParticipant().getState();
            Visit visit = participantState.visits.get(participantState.currentVisit);

            // After the cycle but before the next session
            if (visit.getNumberOfTestsLeft() == visit.getNumberOfTests()) {

                String language = PreferencesManager.getInstance().getString(Locale.TAG_LANGUAGE, Locale.LANGUAGE_ENGLISH);
                String country = PreferencesManager.getInstance().getString(Locale.TAG_COUNTRY, Locale.COUNTRY_UNITED_STATES);
                java.util.Locale locale = new java.util.Locale(language, country);
                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);

                //String format = ViewUtil.getString(com.healthymedium.arc.library.R.string.format_date);
                header = res.getString(R.string.thankyou_header2);
                subheader = res.getString(R.string.thankyou_cycle_subhead2);

                String body2 = res.getString(R.string.thankyou_cycle_body2);

                // String startDate = visit.getActualStartDate().toString(format);
                // String endDate = visit.getActualEndDate().toString(format);

                String startDate = fmt.print(visit.getActualStartDate());
                String endDate = fmt.print(visit.getActualEndDate().minusDays(1));

                body2 = body2.replace("{DATE1}", startDate);
                body2 = body2.replace("{DATE2}", endDate);

                body = body2;
            }
            // After the 4th test of the day
            else if (visit.getNumberOfTestsLeftForToday() == 0) {
                header = res.getString(R.string.thank_you_header1);
                subheader = res.getString(R.string.thankyou_alldone_subhead1);
                body = res.getString(R.string.thankyou_alldone_body1);
            }

        }

        InfoTemplate info = new InfoTemplate(
                false,
                header ,
                subheader,
                body,
                ViewUtil.getDrawable(R.drawable.ic_home_active));
        fragments.add(info);
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void addSchedulePicker() {
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new QuestionAdjustSchedule(false, true, ViewUtil.getString(R.string.ChangeAvail_date_picker_body), ""));

        fragments.add(new ScheduleCalendar());

        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void setPathAdjustSchedule() {
        addSchedulePicker();
    }

    // -----------------------

    public String getLifecycleName(int lifecycle){
        return "";
    }

    public String getPathName(int path){
        return "";
    }

    public State getState(){
        return state;
    }

    public StateCache getCache(){
        return cache;
    }

    // loadTestDataFromCache() is called from abandonTest().
    // Override this method to handle loading test data from cache.
    public void loadTestDataFromCache() {

    }


}
