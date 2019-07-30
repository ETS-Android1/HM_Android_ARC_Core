package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionAdjustSchedule extends QuestionTemplate {

    boolean allowHelp;

    int index = 0;
    int shiftDays = 0;
    int[] shiftAmount = new int[15];


    public QuestionAdjustSchedule(boolean allowBack, boolean allowHelp, String header, String subheader) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_confirm));
        this.allowHelp = allowHelp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(allowHelp);

        NumberPicker picker = new NumberPicker(Application.getInstance());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerTextColor(picker, ContextCompat.getColor(Application.getInstance(), R.color.text));
        content.addView(picker);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                index = newVal;
                enableNextButton();
            }
        });


        Participant participant = Study.getParticipant();
        TestCycle cycle = participant.getCurrentTestCycle();

        DateTime visitStart = cycle.getScheduledStartDate();
        DateTime visitEnd = cycle.getScheduledEndDate();
        String start;
        String end;

        List<String> dataList = new ArrayList<String>();
        int[] tempShiftAmount = new int[15];

        String range;

        String language = PreferencesManager.getInstance().getString(Locale.TAG_LANGUAGE, Locale.LANGUAGE_ENGLISH);
        String country = PreferencesManager.getInstance().getString(Locale.TAG_COUNTRY, Locale.COUNTRY_UNITED_STATES);
        java.util.Locale locale = new java.util.Locale(language, country);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE, MMM d").withLocale(locale);

        int visitAdjustIndex = 0;


        String currStart = fmt.print(cycle.getActualStartDate());
        String currEnd = fmt.print(cycle.getActualEndDate());

        int daysRange = Days.daysBetween(cycle.getActualStartDate().toLocalDate(), cycle.getActualEndDate().toLocalDate()).getDays();
        if (daysRange == 6) {
            currEnd = fmt.print(cycle.getActualEndDate().plusDays(1));
        }

        String currRange = currStart + "-" + currEnd;


        // Build the adjustment ranges

        // Back
        int daysBack = 7;
        while (daysBack > 0) {
            if (!visitStart.minusDays(daysBack).isBeforeNow()) {
                start = fmt.print(visitStart.minusDays(daysBack));
                end = fmt.print(visitEnd.minusDays(daysBack));
                range = start + "-" + end;
                dataList.add(range);
                tempShiftAmount[visitAdjustIndex] = 0 - daysBack;
                visitAdjustIndex++;
            }
            daysBack--;
        }

        // Original range
        if (!visitStart.isBeforeNow()) {
            start = fmt.print(visitStart);
            end = fmt.print(visitEnd);
            range = start + "-" + end;
            dataList.add(range);
            tempShiftAmount[visitAdjustIndex] = 0;
            visitAdjustIndex++;
        }

        // Forward
        int daysForward = 7;
        int count = 1;
        while (count <= daysForward) {
            if (!visitStart.plusDays(count).isBeforeNow()) {
                start = fmt.print(visitStart.plusDays(count));
                end = fmt.print(visitEnd.plusDays(count));
                range = start + "-" + end;
                dataList.add(range);
                tempShiftAmount[visitAdjustIndex] = count;
                visitAdjustIndex++;
            }
            count++;
        }

        int curr = dataList.indexOf(currRange);

        String[] data = new String[dataList.size()];
//        for (int i = 0; i < dataList.size(); i++) {
//            data[i] = dataList.get(i);
//        }

        // Start at the currently schedule window
        data[0] = dataList.get(curr);

        // Need to change all of the shiftAmount values
        // They're not guaranteed to be in the same order they were created
        // This is because we don't know at which date we're starting
        shiftAmount[0] = tempShiftAmount[curr];
        int dataIndex = 1;


        // Get the indices after current
        for (int i = curr+1; i < dataList.size(); i++) {
            data[dataIndex] = dataList.get(i);
            shiftAmount[dataIndex] = tempShiftAmount[i];
            dataIndex++;
        }

        // And lastly get those before current
        for (int j = 0; j < curr; j++) {
            data[dataIndex] = dataList.get(j);
            shiftAmount[dataIndex] = tempShiftAmount[j];
            dataIndex++;
        }

        picker.setMinValue(0);
        picker.setMaxValue(data.length-1);
        picker.setDisplayedValues(data);

        return view;
    }

    @Override
    public Object onValueCollection(){

        return shiftAmount[index];

    }

    public void updateDates() {

        TestCycle cycle = Study.getCurrentTestCycle();
        List<TestSession> sessions = cycle.getTestSessions();

        Study.getScheduler().unscheduleNotifications(cycle);

        for (int i = 0; i < sessions.size(); i++) {
            LocalDate date = sessions.get(i).getPrescribedTime().plusDays(shiftDays).toLocalDate();
            sessions.get(i).setScheduledDate(date);
        }

        int last = sessions.size()-1;
        cycle.setActualStartDate(sessions.get(0).getScheduledTime());
        cycle.setActualEndDate(sessions.get(last).getScheduledTime().plusDays(1));

        Study.getScheduler().scheduleNotifications(cycle, false);
    }

    public void enableNextButton() {
        if(!buttonNext.isEnabled()){
            buttonNext.setEnabled(true);
            onNextButtonEnabled(true);

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shiftDays = (int) onValueCollection();
                    updateDates();
                    Study.getRestClient().submitTestSchedule();
                    onNextRequested();
                }
            });
        }
    }

    public void setNumberPickerTextColor(NumberPicker numberPicker, int color) {

        try{
            Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
        }
        catch(Exception e){
            // nothing
        }

        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                ((EditText) child).setTextColor(color);
            }
        }
        numberPicker.invalidate();

    }

}
