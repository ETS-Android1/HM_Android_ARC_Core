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

public class WeekProgressView extends RelativeLayout {
    private String[] days = new String[]{"S", "M", "T", "W", "T", "F", "S"};
    private int currentDay = 0;
    private int indicatorWidth;
    private Integer difference;

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

        LinearLayout.LayoutParams dayTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        dayTextParams.weight = 1;

        for(String day : days) {
            TextView dayTextView = new TextView(context);
            dayTextView.setLayoutParams(dayTextParams);
            dayTextView.setTypeface(Fonts.robotoBold);
            dayTextView.setTextSize(16);
            dayTextView.setTextColor(ViewUtil.getColor(context, R.color.text));
            dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayTextView.setText(day);
            dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
            dayLayout.addView(dayTextView);
        }

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

        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK);
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
            int dayWidth = (int)(width/7.0);

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

                FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams(indicatorWidth, indicatorWidth);
                indicatorLayout.setLayoutParams(indicatorParams);
                indicatorTextView.setLayoutParams(indicatorParams);
                indicatorTextView.setText(days[currentDay]);
                indicatorLayout.setX((dayWidth * currentDay));
                addView(indicatorLayout);


                initialized = true;
            }
        }
    }
}
