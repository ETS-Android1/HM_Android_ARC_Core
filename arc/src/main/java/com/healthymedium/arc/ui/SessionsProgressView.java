package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.ChipFrameLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class SessionsProgressView extends LinearProgressView {
    public SessionsProgressView(Context context) {
        super(context);
    }

    public SessionsProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SessionsProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        backgroundBorderColor = R.color.sessionProgressBackground;
        backgroundFillColor = R.color.sessionProgressBackground;
        progressColor = R.color.weekProgressFill;
        indicatorColor = R.color.weekProgressFill;
        indicatorTextColor = R.color.white;
        indicatorWidth = ViewUtil.dpToPx(36);

        RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(indicatorWidth, indicatorWidth);
        indicatorParams.addRule(ALIGN_PARENT_END);
        ChipFrameLayout endNode = new ChipFrameLayout(context);
        endNode.setFillColor(backgroundFillColor);
        endNode.setLayoutParams(indicatorParams);
        addView(endNode);

        super.init(context);
        backgroundLayout.setMinimumHeight(ViewUtil.dpToPx(12));
        progressLayout.setMinimumHeight(ViewUtil.dpToPx(12));
    }

    @Override
    protected void initOnMeasure() {
        progressText = String.valueOf(progress);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(unitWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(int i=0; i<=progress; i++) {
            View view = new View(getContext());
            view.setLayoutParams(params);
            progressLayout.addView(view);
        }

        super.initOnMeasure();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        for(int i=0; i<=maxValue; i++) {
            View view = new View(getContext());
            view.setLayoutParams(params);
            backgroundLayout.addView(view);
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
