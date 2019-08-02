package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.Calendar;
import java.util.HashMap;

/*
    Displays days of week with indicator circle on current day.

    Usage:
        Define in XML:
            <com.healthymedium.arc.ui.WeekProgressView
                android:id="@+id/weekProgressView"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

        Set days array in Java:
            WeekProgressView weekProgressView = view.findViewById(R.id.weekProgressView);
            weekProgressView.setDays(new String[]{"M", "T", "W", "T", "F"});

        Current day of the week will highlight itself
*/

public class WeekProgressView extends LinearProgressView {

    private HashMap<Integer, Integer> dayMap = new HashMap<>();
    private String[] defaultDays = new String[]{"S", "M", "T", "W", "T", "F", "S"};
    private String[] days;
    protected int currentDay = 0;

    public WeekProgressView(Context context) {
        super(context);
    }

    public WeekProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        backgroundBorderColor = R.color.weekProgressBorder;
        progressColor = R.color.weekProgressFill;
        indicatorColor = R.color.weekProgressFill;
        indicatorTextColor = R.color.white;
        indicatorWidth = ViewUtil.dpToPx(50);
        super.init(context);
    }

    @Override
    protected void initOnMeasure() {
        if(padding == null) {
                indicatorWidth = Math.max(indicatorWidth, unitWidth);
            padding = 0;
            if (indicatorWidth > unitWidth) {
                padding = (indicatorWidth - unitWidth) / 2;
            }
            backgroundLayout.setPadding(padding, 0, padding, 0);
        }

        for (int i = 0; i <= currentDay; i++) {
            int width = backgroundLayout.getChildAt(i).getMeasuredWidth() - padding*2/7;
            LinearLayout.LayoutParams dayTextParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            String day = days[i];
            TextView dayTextView = new TextView(getContext());
            dayTextView.setLayoutParams(dayTextParams);
            dayTextView.setTypeface(Fonts.robotoBold);
            dayTextView.setTextSize(16);
            dayTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.white));
            dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayTextView.setText(day);
            dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
            progressLayout.addView(dayTextView);
        }
        progressLayout.setPadding(padding, 0, padding, 0);

        progressText = days[currentDay];

        super.initOnMeasure();
    }


    private void buildView() {
        LinearLayout.LayoutParams dayTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        dayTextParams.weight = 1;
        for(String day : days) {
            TextView dayTextView = new TextView(getContext());
            dayTextView.setLayoutParams(dayTextParams);
            dayTextView.setTypeface(Fonts.robotoBold);
            dayTextView.setTextSize(16);
            dayTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.text));
            dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayTextView.setText(day);
            dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
            backgroundLayout.addView(dayTextView);
        }
    }

    private void parseDays() {
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for(int i=0; i < days.length; i++) {
            int nextDay = (i == days.length-1) ? 0:i+1;
            String a = days[i];
            String b = days[nextDay];

            if(a.equals(getContext().getResources().getString(R.string.Day_Abbrev_Sun))) {
                if(b.equals(getContext().getResources().getString(R.string.Day_Abbrev_Mon))) {
                    dayMap.put(0, i);
                } else {
                    dayMap.put(6, i);
                }
            } else if(a.equals(getContext().getResources().getString(R.string.Day_Abbrev_Tue))) {
                if(b.equals(getContext().getResources().getString(R.string.Day_Abbrev_Wed))) {
                    dayMap.put(2, i);
                } else {
                    dayMap.put(4, i);
                }
            } else if(a.equals(getContext().getResources().getString(R.string.Day_Abbrev_Mon))) {
                dayMap.put(1, i);
            } else if(a.equals(getContext().getResources().getString(R.string.Day_Abbrev_Wed))) {
                dayMap.put(3, i);
            } else if(a.equals(getContext().getResources().getString(R.string.Day_Abbrev_Fri))) {
                dayMap.put(5, i);
            }
        }

        currentDay = dayMap.get(currentDay);

        progress = currentDay;
    }

    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) {
        this.days = days;
        maxValue = days.length-1;
        parseDays();
        buildView();
    }
}
