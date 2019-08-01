package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.WeekProgressView;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ProgressScreen extends BaseFragment {

    TextView weeklyStatus;
    WeekProgressView weekProgressView;
    TextView startDate;
    TextView startDate_date;
    TextView endDate;
    TextView endDate_date;
    TextView studyStatus;
    TextView joinedDate;
    TextView joinedDate_date;
    TextView finishDate;
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

        int currDay = participant.getCurrentTestDay().getDayIndex();

        weeklyStatus = view.findViewById(R.id.weeklyStatus);
        weeklyStatus.setText(Html.fromHtml(ViewUtil.getString(R.string.progess_weeklystatus).replace("{#}", Integer.toString(currDay))));

        weekProgressView = view.findViewById(R.id.weekProgressView);
        weekProgressView.setDays(new String[]{"S", "M", "T", "W", "T", "F", "S"});



        // Start and end dates for current week

        startDate = view.findViewById(R.id.startDate);
        startDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_startdate)));

        TestCycle cycle = participant.getCurrentTestCycle();

        DateTime visitStart = cycle.getActualStartDate();
        DateTime visitEnd = cycle.getActualEndDate();

        String language = com.healthymedium.arc.utilities.PreferencesManager.getInstance().getString(Locale.TAG_LANGUAGE, Locale.LANGUAGE_ENGLISH);
        String country = PreferencesManager.getInstance().getString(Locale.TAG_COUNTRY, Locale.COUNTRY_UNITED_STATES);
        java.util.Locale locale = new java.util.Locale(language, country);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);

        String start = fmt.print(visitStart);
        String end = fmt.print(visitEnd.minusDays(1));

        startDate_date = view.findViewById(R.id.startDate_date);
        startDate_date.setText(ViewUtil.getString(R.string.progress_startdate_date).replace("{DATE}", start));

        endDate = view.findViewById(R.id.endDate);
        endDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_enddate)));

        endDate_date = view.findViewById(R.id.endDate_date);
        endDate_date.setText(ViewUtil.getString(R.string.progress_enddate_date).replace("{DATE}", end));



        // Study dates and times

        studyStatus = view.findViewById(R.id.studyStatus);
        int currCycle = cycle.getId()+1; // Cycles are 0-indexed
        studyStatus.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_studystatus).replace("{#}", Integer.toString(currCycle))));

        joinedDate = view.findViewById(R.id.joinedDate);
        joinedDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_joindate)));

        // TODO
        // The join date should be the start date of test cycle 0
        joinedDate_date = view.findViewById(R.id.joinedDate_date);
        joinedDate_date.setText(ViewUtil.getString(R.string.progress_joindate_date).replace("{DATE}", "x"));

        finishDate = view.findViewById(R.id.finishDate);
        finishDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_finishdate)));

        // TODO
        // The finish date should be the end date of test cycle 11
        finishDate_date = view.findViewById(R.id.finishDate_date);
        finishDate_date.setText(ViewUtil.getString(R.string.progress_finishdate_date).replace("{DATE}", "x"));

        timeBetween = view.findViewById(R.id.timeBetween);
        timeBetween.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_timebtwtesting)));

        timeBetween_units = view.findViewById(R.id.timeBetween_units);
        String units = ViewUtil.getString(R.string.progress_timebtwtesting_unit);
        // TODO: We know the number and units in advance when we schedule
        // Make a function to grab them
        units = units.replace("{#}", "6");
        units = units.replace("{UNIT}", "months");
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
        getMainActivity().showNavigationBar();
    }
}
