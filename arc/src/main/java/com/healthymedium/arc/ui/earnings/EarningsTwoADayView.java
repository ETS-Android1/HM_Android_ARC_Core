package com.healthymedium.arc.ui.earnings;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.util.MeasureUnit;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.EarningsScreen;
import com.healthymedium.arc.paths.informative.ProgressScreen;
import com.healthymedium.arc.paths.informative.ResourcesScreen;
import com.healthymedium.arc.paths.templates.LandingTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.ui.CircleProgressView;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import java.util.List;

public class EarningsTwoADayView extends LinearLayout {

    public EarningsTwoADayView(Context context, EarningOverview.Goals.Goal goal, int cycleIndex) {
        super(context);

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        int normalColor = ViewUtil.getColor(getContext(),R.color.white);
        int highlightColor = ViewUtil.getColor(getContext(),R.color.progressWeekBackground);

        int colors[] = new int[]{normalColor,highlightColor};
        boolean highlight = true;

        int abbreviations[] = new int[]{
                R.string.Day_Abbrev_Sun,
                R.string.Day_Abbrev_Mon,
                R.string.Day_Abbrev_Tue,
                R.string.Day_Abbrev_Wed,
                R.string.Day_Abbrev_Thur,
                R.string.Day_Abbrev_Fri,
                R.string.Day_Abbrev_Sat
        };

        TestCycle cycle = Study.getParticipant().getState().testCycles.get(cycleIndex);
        DateTime day = cycle.getActualStartDate();

        if(cycleIndex==0){
            day = day.plusDays(1);
        }

        List<Integer> components = goal.progress_components;
        for(int i=0;i<components.size();i++){

            int dayOfWeek = day.getDayOfWeek();
            if(dayOfWeek==7){
                dayOfWeek = 0;
            }
            day = day.plusDays(1);
            String abbr = ViewUtil.getString(abbreviations[dayOfWeek]);

            int bgColor = colors[highlight ? 1:0];
            highlight = !highlight;

            DayView dayView = new DayView(context,bgColor,components.get(i),abbr);
            addView(dayView);
        }

    }

    public class DayView extends LinearLayout {

        public DayView(Context context, int bgColor, int progress, String abbr) {
            super(context);

            int dp8 = ViewUtil.dpToPx(8);
            int dp24 = ViewUtil.dpToPx(24);

            setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,ViewUtil.dpToPx(72)));
            setBackgroundColor(bgColor);
            setGravity(Gravity.CENTER);
            setOrientation(VERTICAL);

            CircleProgressView progressView = new CircleProgressView(context);
            progressView.setLayoutParams(new LayoutParams(dp24,dp24));
            progressView.setProgress(progress,false);
            progressView.setMargin(dp8,0,dp8,dp8);
            progressView.setStrokeWidth(ViewUtil.dpToPx(1));
            progressView.setBaseColor(R.color.secondaryAccent);
            progressView.setCheckmarkColor(R.color.secondaryAccent);
            progressView.setShadowColor(R.color.white);
            progressView.setSweepColor(R.color.secondary);
            addView(progressView);

            TextView textView = new TextView(context);
            textView.setTextColor(ViewUtil.getColor(R.color.secondaryDark));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
            textView.setTypeface(Fonts.robotoBold);
            textView.setText(abbr);
            textView.setGravity(Gravity.CENTER);
            addView(textView);
        }
    }

}
