package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.ChipFrameLayout;
import com.healthymedium.arc.ui.base.ChipLinearLayout;
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

public class WeekProgressView extends RelativeLayout {

    private HashMap<String, Integer> dayMap = new HashMap<String, Integer>(){{
        put(getContext().getResources().getString(R.string.Day_Abbrev_Sun), 0);
        put(getContext().getResources().getString(R.string.Day_Abbrev_Mon), 1);
        put(getContext().getResources().getString(R.string.Day_Abbrev_Tue), 2);
        put(getContext().getResources().getString(R.string.Day_Abbrev_Wed), 3);
        put(getContext().getResources().getString(R.string.Day_Abbrev_Thur), 4);
        put(getContext().getResources().getString(R.string.Day_Abbrev_Fri), 5);
        put(getContext().getResources().getString(R.string.Day_Abbrev_Sat), 6);
    }};
    private String[] days;
    private int currentDay = 0;
    private int indicatorWidth;
    private Integer difference;
    private int dayWidth;

    private ChipLinearLayout dayLayout;
    private ChipLinearLayout completedDayLayout;
    private ChipFrameLayout indicatorLayout;
    private TextView indicatorTextView;

    private boolean initialized = false;

    public WeekProgressView(Context context) {
        super(context);
        init(context);
    }

    public WeekProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeekProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        RelativeLayout.LayoutParams matchWrapParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams wrapWrapParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        matchWrapParams.addRule(RelativeLayout.CENTER_VERTICAL);
        wrapWrapParams.addRule(RelativeLayout.CENTER_VERTICAL);

        dayLayout = new ChipLinearLayout(context);
        dayLayout.setLayoutParams(matchWrapParams);
        dayLayout.setOrientation(LinearLayout.HORIZONTAL);
        dayLayout.setStrokeColor(R.color.weekProgressBorder);
        dayLayout.setStrokeWidth(1);

        completedDayLayout = new ChipLinearLayout(context);
        completedDayLayout.setLayoutParams(wrapWrapParams);
        completedDayLayout.setOrientation(LinearLayout.HORIZONTAL);
        completedDayLayout.setFillColor(R.color.weekProgressFill);
        completedDayLayout.setStrokeColor(R.color.weekProgressFill);
        completedDayLayout.setStrokeWidth(1);

        addView(dayLayout);

        indicatorWidth = ViewUtil.dpToPx(50);
        indicatorLayout = new ChipFrameLayout(context);
        indicatorLayout.setFillColor(R.color.weekProgressFill);

        indicatorTextView = new TextView(context);
        indicatorTextView.setTypeface(Fonts.robotoBold);
        indicatorTextView.setTextSize(16);
        indicatorTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.white));
        indicatorTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        indicatorTextView.setGravity(Gravity.CENTER_VERTICAL);

        indicatorLayout.addView(indicatorTextView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }

        if(width > 0 && !initialized) {
            dayWidth = (width/days.length);

            if(difference == null) {
                indicatorWidth = Math.max(indicatorWidth, dayWidth);
                difference = 0;
                if (indicatorWidth - dayWidth > 0) {
                    difference = (indicatorWidth - dayWidth) / 2;
                }

                dayLayout.setPadding(difference, 0, difference, 0);
                completedDayLayout.setPadding(difference, 0, difference, 0);
            } else {
                LinearLayout.LayoutParams dayTextParams = new LinearLayout.LayoutParams(dayWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

                for (int i = 0; i <= currentDay; i++) {
                    String day = days[i];
                    TextView dayTextView = new TextView(getContext());
                    dayTextView.setLayoutParams(dayTextParams);
                    dayTextView.setTypeface(Fonts.robotoBold);
                    dayTextView.setTextSize(16);
                    dayTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.white));
                    dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    dayTextView.setText(day);
                    dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
                    completedDayLayout.addView(dayTextView);
                }

                addView(completedDayLayout);

                if(currentDay >= 0) {
                    FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams(indicatorWidth, indicatorWidth);
                    indicatorLayout.setLayoutParams(indicatorParams);
                    indicatorTextView.setLayoutParams(indicatorParams);
                    indicatorTextView.setText(days[currentDay]);
                    indicatorLayout.setX((dayWidth * currentDay));
                    addView(indicatorLayout);
                }

                initialized = true;
            }
        }
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
            dayLayout.addView(dayTextView);
        }
    }

    private void parseDays() {
        String firstDay = days[0];
        String nextDay = days[1];
        int dayOffset = 0;

        if(firstDay.equals(getContext().getResources().getString(R.string.Day_Abbrev_Sun))) {
            if(nextDay.equals(getContext().getResources().getString(R.string.Day_Abbrev_Sun))) {
                dayOffset = 6;
            }
        } else if(firstDay.equals(getContext().getResources().getString(R.string.Day_Abbrev_Tue))) {
            dayOffset = 2;
            if(nextDay.equals(getContext().getResources().getString(R.string.Day_Abbrev_Fri))) {
                dayOffset = 4;
            }
        } else {
            dayOffset = dayMap.get(firstDay);
        }

        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK) - dayOffset;
    }

    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) {
        this.days = days;
        parseDays();
        buildView();
    }
}
