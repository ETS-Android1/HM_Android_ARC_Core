package com.healthymedium.arc.study;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.SimplePopupScreen;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.AvailabilityPathData;
import com.healthymedium.arc.path_data.ChronotypePathData;
import com.healthymedium.arc.path_data.ContextPathData;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.path_data.PriceTestPathData;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.path_data.SymbolsTestPathData;
import com.healthymedium.arc.path_data.WakePathData;
import com.healthymedium.arc.paths.availability.AvailabilityMondayBed;
import com.healthymedium.arc.paths.availability.AvailabilityMondayWake;
import com.healthymedium.arc.paths.availability.AvailabilityOtherBed;
import com.healthymedium.arc.paths.availability.AvailabilityOtherWake;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySaturdayWake;
import com.healthymedium.arc.paths.availability.AvailabilitySundayBed;
import com.healthymedium.arc.paths.availability.AvailabilitySundayWake;
import com.healthymedium.arc.paths.availability.AvailabilityWeekdayConfirm;
import com.healthymedium.arc.paths.informative.ScheduleCalendar;
import com.healthymedium.arc.paths.questions.QuestionAdjustSchedule;
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
import com.healthymedium.arc.paths.setup.SetupSite;
import com.healthymedium.arc.paths.setup.SetupWelcome;
import com.healthymedium.arc.paths.tests.GridLetters;
import com.healthymedium.arc.paths.tests.GridStudy;
import com.healthymedium.arc.paths.tests.GridTest;
import com.healthymedium.arc.paths.tests.PriceTestCompareFragment;
import com.healthymedium.arc.paths.tests.PriceTestMatchFragment;
import com.healthymedium.arc.paths.tests.QuestionInterrupted;
import com.healthymedium.arc.paths.tests.SymbolTest;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.PriceManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StudyStateMachine {

    public static final String TAG_STUDY_STATE_CACHE = "StudyStateCache";
    public static final String TAG_STUDY_STATE = "StudyState";

    protected String tag = getClass().getSimpleName();

    protected StudyState state;
    protected StudyStateCache cache;
    protected boolean currentlyInTestPath = false;

    public StudyStateMachine() {

    }

    protected void enableTransition(PathSegment segment, boolean animateEntry){
        int size = segment.fragments.size();
        int first = 0;
        int last = size-1;

        for(int i=0;i<size;i++){
            if(i!=first || animateEntry){
                segment.fragments.get(i).setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
            }
            segment.fragments.get(i).setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);
        }
    }

    protected void enableTransitionGrids(PathSegment segment, boolean animateEntry){
        if (animateEntry) {
            segment.fragments.get(1).setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
            segment.fragments.get(1).setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);

            segment.fragments.get(2).setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
            segment.fragments.get(2).setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);

            segment.fragments.get(3).setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
            segment.fragments.get(3).setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);
        }
    }

    public void initialize(){
        state = new StudyState();
        cache = new StudyStateCache();
    }

    public void load(){
        load(false);
    }

    public void load(boolean overwrite){
        Log.d(tag,"load(overwrite = "+overwrite+")");
        if(state!=null && !overwrite){
            return;
        }
        state = PreferencesManager.getInstance().getObject(TAG_STUDY_STATE,StudyState.class);
        cache = CacheManager.getInstance().getObject(TAG_STUDY_STATE_CACHE,StudyStateCache.class);
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
        PreferencesManager.getInstance().putBoolean("TestCompleteFlag",complete);
    }

    protected boolean isTestCompleteFlagSet(){
        return PreferencesManager.getInstance().getBoolean("TestCompleteFlag",false);
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

    // -----------------------------------------------------------------


    public void setPathSetupParticipant(){
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new SetupWelcome());
        fragments.add(new SetupParticipant());
        fragments.add(new SetupParticipantConfirm());
        fragments.add(new SetupSite());

        PathSegment segment = new PathSegment(fragments,SetupPathData.class);
        enableTransition(segment,false);
        cache.segments.add(segment);
    }

    public void setPathSetupParticipant(int firstDigits, int secondDigits, int siteDigits) {
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new SetupWelcome());

        SetupParticipant setupParticipantFragment = new SetupParticipant();
        Bundle setupDigitsBundle = new Bundle();
        setupDigitsBundle.putInt("firstDigits", firstDigits);
        setupDigitsBundle.putInt("secondDigits", secondDigits);
        setupParticipantFragment.setArguments(setupDigitsBundle);
        fragments.add(setupParticipantFragment);

        SetupParticipantConfirm setupParticipantConfirmFragment = new SetupParticipantConfirm();
        setupParticipantConfirmFragment.setArguments(setupDigitsBundle);
        fragments.add(setupParticipantConfirmFragment);

        SetupSite setupSiteFragment = new SetupSite();
        Bundle setupSiteBundle = new Bundle();
        setupSiteBundle.putInt("siteDigits", siteDigits);
        setupSiteFragment.setArguments(setupSiteBundle);
        fragments.add(setupSiteFragment);

        PathSegment segment = new PathSegment(fragments,SetupPathData.class);
        enableTransition(segment,false);
        cache.segments.add(segment);
    }

    public void setPathSetupAvailability(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new InfoTemplate(
                false,
                res.getString(R.string.setup_avail_header) ,
                res.getString(R.string.setup_avail_subheader),
                res.getString(R.string.setup_avail_body),
                res.getString(R.string.button_begin)));

        fragments.add(new AvailabilityMondayWake());
        fragments.add(new AvailabilityMondayBed());
        fragments.add(new AvailabilityWeekdayConfirm());
        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_tuesday)));
        fragments.add(new AvailabilityOtherBed(res.getString(R.string.weekday_tuesday)));
        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_wednesday)));
        fragments.add(new AvailabilityOtherBed(res.getString(R.string.weekday_wednesday)));
        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_thursday)));
        fragments.add(new AvailabilityOtherBed(res.getString(R.string.weekday_thursday)));
        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_friday)));
        fragments.add(new AvailabilityOtherBed(res.getString(R.string.weekday_friday)));
        fragments.add(new AvailabilitySaturdayWake());
        fragments.add(new AvailabilitySaturdayBed());
        fragments.add(new AvailabilitySundayWake());
        fragments.add(new AvailabilitySundayBed());

        PathSegment segment = new PathSegment(fragments,AvailabilityPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void setPathSetupAvailability(int minWakeTime, int maxWakeTime, boolean reschedule){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new InfoTemplate(
                false,
                res.getString(R.string.setup_avail_header),
                res.getString(R.string.setup_avail_subheader),
                res.getString(R.string.setup_avail_body),
                res.getString(R.string.button_begin)));

        Bundle windowBundle = new Bundle();
        windowBundle.putInt("minWakeTime", minWakeTime);
        windowBundle.putInt("maxWakeTime", maxWakeTime);
        windowBundle.putBoolean("reschedule", reschedule);

        fragments.add(new AvailabilityMondayWake());

        AvailabilityMondayBed mondayBed = new AvailabilityMondayBed();
        mondayBed.setArguments(windowBundle);
        fragments.add(mondayBed);

        fragments.add(new AvailabilityWeekdayConfirm());

        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_tuesday)));

        AvailabilityOtherBed tuesdayBed = new AvailabilityOtherBed(res.getString(R.string.weekday_tuesday));
        tuesdayBed.setArguments(windowBundle);
        fragments.add(tuesdayBed);

        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_wednesday)));

        AvailabilityOtherBed wednesdayBed = new AvailabilityOtherBed(res.getString(R.string.weekday_wednesday));
        wednesdayBed.setArguments(windowBundle);
        fragments.add(wednesdayBed);

        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_thursday)));

        AvailabilityOtherBed thursdayBed = new AvailabilityOtherBed(res.getString(R.string.weekday_thursday));
        thursdayBed.setArguments(windowBundle);
        fragments.add(thursdayBed);

        fragments.add(new AvailabilityOtherWake(res.getString(R.string.weekday_friday)));

        AvailabilityOtherBed fridayBed = new AvailabilityOtherBed(res.getString(R.string.weekday_friday));
        fridayBed.setArguments(windowBundle);
        fragments.add(fridayBed);

        fragments.add(new AvailabilitySaturdayWake());

        AvailabilitySaturdayBed saturdayBed = new AvailabilitySaturdayBed();
        saturdayBed.setArguments(windowBundle);
        fragments.add(saturdayBed);

        fragments.add(new AvailabilitySundayWake());

        AvailabilitySundayBed sundayBed = new AvailabilitySundayBed();
        sundayBed.setArguments(windowBundle);
        fragments.add(sundayBed);

        PathSegment segment = new PathSegment(fragments,AvailabilityPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addChronotypeSurvey(){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new InfoTemplate(
                false,
                res.getString(R.string.chronotype_header),
                res.getString(R.string.chronotype_subheader),
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

        fragments.add(new QuestionRadioButtons(true,false, res.getString(R.string.chronotype_1_q2), res.getString(R.string.chronotype_1_q2_sub ),workingDayCountOptions));

        fragments.add(new InfoTemplate(
                false,
                res.getString(R.string.chronotype_header),
                "",
                res.getString(R.string.chronotype_2_body),
                res.getString(R.string.button_next)));

        CircadianClock clock;
        String weekday;
        LocalTime wakeTime = null;
        LocalTime bedTime = null;

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
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

        fragments.add(new InfoTemplate(
                false,
                res.getString(R.string.sleepwakesurvey_header),
                res.getString(R.string.sleepwakesurvey_subhead),
                res.getString(R.string.sleepwakesurvey_body),
                res.getString(R.string.button_begin)));

        CircadianClock clock;
        String weekday;
        LocalTime wakeTime = null;
        LocalTime bedTime = null;

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
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

        fragments.add(new InfoTemplate(
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
        fragments.add(new QuestionCheckBoxes(true, res.getString(R.string.context_q1), res.getString(R.string.context_q1_sub), who, res.getString(R.string.context_q1_answers1)));

        List<String> where = new ArrayList<>();
        where.add(res.getString(R.string.context_q2_answers1));
        where.add(res.getString(R.string.context_q2_answers2));
        where.add(res.getString(R.string.context_q2_answers3));
        where.add(res.getString(R.string.context_q2_answers4));
        where.add(res.getString(R.string.context_q2_answers5));
        where.add(res.getString(R.string.context_q2_answers6));
        where.add(res.getString(R.string.context_q2_answers7));
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q2), res.getString(R.string.context_q2_sub), where));

        fragments.add(new QuestionRating(true, res.getString(R.string.context_q3), res.getString(R.string.context_q3_sub), res.getString(R.string.context_bad), res.getString(R.string.context_good)));
        fragments.add(new QuestionRating(true, res.getString(R.string.context_q4), res.getString(R.string.context_q4_sub), res.getString(R.string.context_tired), res.getString(R.string.context_active)));

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
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q5), res.getString(R.string.context_q5_sub), what));

        PathSegment segment = new PathSegment(fragments,ContextPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addTests(){
        PreferencesManager.getInstance().putInt("test_missed_count", 0);

        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        InfoTemplate info = new InfoTemplate(
                false,
                res.getString(R.string.testing_intro_header),
                res.getString(R.string.testing_intro_subhead),
                res.getString(R.string.testing_intro_body),
                res.getString(R.string.button_next));
        //info.setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
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

        InfoTemplate info = new InfoTemplate(
                false,
                "Test "+(index+1)+" of 3" ,
                res.getString(R.string.price_header),
                res.getString(R.string.price_body),
                res.getString(R.string.button_begin));
        fragments.add(info);

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

        PathSegment segment = new PathSegment(fragments,PriceTestPathData.class);
        cache.segments.add(segment);
    }

    public void addSymbolsTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        InfoTemplate info = new InfoTemplate(
                false,
                "Test "+(index+1)+" of 3" ,
                res.getString(R.string.symbols_header),
                res.getString(R.string.symbols_body),
                res.getString(R.string.button_begin));
        fragments.add(info);

        fragments.add(new SymbolTest());

        PathSegment segment = new PathSegment(fragments,SymbolsTestPathData.class);
        cache.segments.add(segment);
    }

    public void addGridTest(int index){
        List<BaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        InfoTemplate info0 = new InfoTemplate(
                false,
                "Test "+(index+1)+" of 3" ,
                res.getString(R.string.grid_header),
                res.getString(R.string.grid_body1),
                res.getString(R.string.button_next));
        fragments.add(info0);

        InfoTemplate info1 = new InfoTemplate(
                true,
                "Test "+(index+1)+" of 3" ,
                res.getString(R.string.grid_header),
                res.getString(R.string.grid_body2),
                res.getString(R.string.button_next));
        fragments.add(info1);

        InfoTemplate info2 = new InfoTemplate(
                true,
                "Test "+(index+1)+" of 3" ,
                res.getString(R.string.grid_header),
                res.getString(R.string.grid_body3),
                res.getString(R.string.button_begin));
        fragments.add(info2);

        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        fragments.add(new GridTest());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        GridTest gridTestFragment = new GridTest();
        gridTestFragment.second = true;
        fragments.add(gridTestFragment);

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
        header = res.getString(R.string.thankyou_header1);
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
                String format = ViewUtil.getString(com.healthymedium.arc.library.R.string.format_date);
                String date = visit.getActualStartDate().toString(format);
                header = res.getString(R.string.thankyou_header2);
                subheader = res.getString(R.string.thankyou_cycle_subhead2);

                String body2_1 = res.getString(R.string.thankyou_cycle_body2_1);
                String body2_2 = res.getString(R.string.thankyou_cycle_body2_2);

                body = body2_1 + date + body2_2;
            }
            // After the 4th test of the day
            else if (visit.getNumberOfTestsLeftForToday() == 0) {
                header = res.getString(R.string.thankyou_header1);
                subheader = res.getString(R.string.thankyou_alldone_subhead1);
                body = res.getString(R.string.thankyou_alldone_body1);
            }

        }

        InfoTemplate info = new InfoTemplate(
                false,
                header ,
                subheader,
                body,
                res.getString(R.string.thankyou_button_return_to_home));
        fragments.add(info);
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    public void addSchedulePicker() {
        List<BaseFragment> fragments = new ArrayList<>();

        fragments.add(new QuestionAdjustSchedule(false, true, "What week are you able to test?", ""));

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

    public StudyState getState(){
        return state;
    }

    // loadTestDataFromCache() is called from abandonTest().
    // Override this method to handle loading test data from cache.
    public void loadTestDataFromCache() {

    }


}
