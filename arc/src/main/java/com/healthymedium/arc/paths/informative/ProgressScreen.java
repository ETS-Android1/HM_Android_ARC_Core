package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.CircleProgressView;
import com.healthymedium.arc.ui.WeekProgressView;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;

import org.joda.time.DateTime;
import org.joda.time.Months;

public class ProgressScreen extends BaseFragment {

    TestCycle testCycle;
    TestDay testDay;
    TestSession testSession;

    boolean isPractice = false;
    boolean isBaseline = false;


    TextView studyStatus;
    TextView joinedDate_date;
    TextView finishDate_date;
    TextView timeBetween;
    TextView timeBetween_units;
    Button viewFaqButton;

    public ProgressScreen() {
        allowBackPress(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        Participant participant = Study.getParticipant();
        testCycle = participant.getCurrentTestCycle();
        testDay = participant.getCurrentTestDay();
        testSession = participant.getCurrentTestSession();

        ParticipantState state = participant.getState();
        int sessionIndex = state.currentTestSession-1;
        int dayIndex = state.currentTestDay;
        int cycleIndex = state.currentTestCycle;

        if(testDay.getStartTime().isAfterNow()) {
            if(sessionIndex<0) {
                dayIndex--;
                if (dayIndex < 0) {
                    cycleIndex--;
                    testCycle = state.testCycles.get(cycleIndex);
                    dayIndex = testCycle.getNumberOfTestDays() - 1;
                }
                testDay = testCycle.getTestDay(dayIndex);
                sessionIndex = testDay.getNumberOfTests() - 1;
                testSession = testDay.getTestSession(sessionIndex);
            }
        }

        isPractice = (dayIndex==0 && sessionIndex==0 && cycleIndex==0);
        isBaseline = (cycleIndex==0);

        if(testCycle.getActualStartDate().isBeforeNow() && testCycle.getActualEndDate().isAfterNow()) {
            setupTodaysSessions(view, testDay);
            setupWeekView(view, testCycle, testDay);
        }

        // study view partition

        int currCycle = testCycle.getId()+1; // Cycles are 0-indexed
        String status = ViewUtil.getString(R.string.progress_studystatus);
        studyStatus = view.findViewById(R.id.studyStatus);
        studyStatus.setText(Html.fromHtml(ViewUtil.replaceToken(status,R.string.token_number,Integer.toString(currCycle))));

        // The join date should be the start date of test cycle 0
        DateTime joinedDate = Study.getParticipant().getStartDate();
        String joinedDateString = JodaUtil.format(joinedDate,R.string.format_date_longer);
        String joinedString = getString(R.string.progress_joindate_date);
        joinedString = ViewUtil.replaceToken(joinedString,R.string.token_date,joinedDateString);
        joinedDate_date = view.findViewById(R.id.joinedDate_date);
        joinedDate_date.setText(joinedString);

        DateTime finishDate = Study.getParticipant().getFinishDate();
        String finishDateString = JodaUtil.format(finishDate,R.string.format_date_longer);
        String finishString = getString(R.string.progress_finishdate_date);
        finishString = ViewUtil.replaceToken(finishString,R.string.token_date,finishDateString);
        finishDate_date = view.findViewById(R.id.finishDate_date);
        finishDate_date.setText(finishString);

        timeBetween = view.findViewById(R.id.timeBetween);
        timeBetween.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_timebtwtesting)));

        DateTime endOfFirstCycle = state.testCycles.get(0).getActualEndDate();
        DateTime startOfSecondCycle = state.testCycles.get(1).getActualStartDate();

        int monthsBetween = Months.monthsBetween(endOfFirstCycle,startOfSecondCycle).getMonths();
        timeBetween_units = view.findViewById(R.id.timeBetween_units);
        String units = ViewUtil.getString(R.string.progress_timebtwtesting_unit);
        units = units.replace(getString(R.string.token_number), String.valueOf(monthsBetween));
        units = units.replace(getString(R.string.token_unit), "Months");
        timeBetween_units.setText(units);

        viewFaqButton = view.findViewById(R.id.viewFaqButton);
        viewFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });

        return view;
    }

    private void setupTodaysSessions(View view, TestDay testDay) {

        LinearLayout layout = view.findViewById(R.id.sessions);
        layout.setVisibility(View.VISIBLE);

        LinearLayout sessionProgressLayout = view.findViewById(R.id.sessionProgressLayout);
        int margin = ViewUtil.dpToPx(4);

        for(TestSession session : testDay.getTestSessions()){
            CircleProgressView progressView = new CircleProgressView(getContext());
            sessionProgressLayout.addView(progressView);

            progressView.setBaseColor(R.color.secondaryAccent);
            progressView.setCheckmarkColor(R.color.secondaryAccent);
            progressView.setSweepColor(R.color.secondary);
            progressView.setStrokeWidth(6);
            progressView.setMargin(margin,0,margin,0);
            progressView.setProgress(session.getProgress(), false);
        }

        int testsCompleted = testDay.getNumberOfTestsFinished();
        String complete = getString(R.string.progress_dailystatus_complete);
        complete = ViewUtil.replaceToken(complete,R.string.token_number,String.valueOf(testsCompleted));

        TextView textViewComplete = view.findViewById(R.id.complete);
        textViewComplete.setText(Html.fromHtml(complete));

        int testsRemaining = testDay.getNumberOfTestsLeft();
        String remaining = getString(R.string.progress_dailystatus_remaining);
        remaining = ViewUtil.replaceToken(remaining,R.string.token_number,String.valueOf(testsRemaining));

        TextView textViewRemaining = view.findViewById(R.id.remaining);
        textViewRemaining.setText(Html.fromHtml(remaining));

    }

    private void setupWeekView(View view, TestCycle testCycle, TestDay testDay) {

        LinearLayout layout = view.findViewById(R.id.week);
        layout.setVisibility(View.VISIBLE);



        TextView weeklyStatus = view.findViewById(R.id.weeklyStatus);
        WeekProgressView weekProgressView = view.findViewById(R.id.weekProgressView);
        String daysString;

        int dayIndex = testDay.getDayIndex();
        if(isBaseline){
            dayIndex--;
        }

        if(isPractice){
            daysString = getString(R.string.progress_baseline_notice);
            weeklyStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
            weekProgressView.setVisibility(View.GONE);
        } else {
            daysString = getString(R.string.progess_weeklystatus);
            daysString = ViewUtil.replaceToken(daysString,R.string.token_number,String.valueOf(dayIndex+1));

            DateTime startDate = testCycle.getActualStartDate();
            if(isBaseline){
                startDate = startDate.plusDays(1);
            }
            weekProgressView.setupView(startDate,dayIndex);
        }

        weeklyStatus.setText(Html.fromHtml(daysString));

        TextView startDate = view.findViewById(R.id.startDate);
        startDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_startdate)));

        String start = JodaUtil.format(testCycle.getActualStartDate(),R.string.format_date_long,Application.getInstance().getLocale());
        String end = JodaUtil.format(testCycle.getActualEndDate().minusDays(1),R.string.format_date_long,Application.getInstance().getLocale());

        TextView startDate_date = view.findViewById(R.id.startDate_date);
        startDate_date.setText(getString(R.string.progress_startdate_date).replace("{DATE}", start));

        TextView endDate = view.findViewById(R.id.endDate);
        endDate.setText(Html.fromHtml(getString(R.string.progress_enddate)));

        TextView endDate_date = view.findViewById(R.id.endDate_date);
        endDate_date.setText(getString(R.string.progress_enddate_date).replace("{DATE}", end));

    }


        @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
        getMainActivity().showNavigationBar();
    }
}
