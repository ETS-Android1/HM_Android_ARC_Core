package com.healthymedium.arc.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import java.text.DecimalFormat;

/*
    Displays an initial value of text on screen for 1 second, then text slides up to reveal new text.

    Usage:
        Define in XML:
            <com.healthymedium.arc.ui.TotalEarningsView
                android:id="@+id/totalEarningsView"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

        Set first and second values in Java:
            TotalEarningsView totalEarningsView = view.findViewById(R.id.totalEarningsView);
            totalEarningsView.setOriginalValue(4.10);
            totalEarningsView.setUpdatedValue(7.50);

        The field will animate itself.
*/

public class TotalEarningsView extends LinearLayout {

    private final long animationStartDelayTime = 1000L;
    private int white;
    private double originalValue;
    private double updatedValue;
    private Handler handler;
    TextSwitcher textSwitcher;
    DecimalFormat decimalFormat;

    public TotalEarningsView(Context context) {
        super(context);
        init();
    }

    public TotalEarningsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TotalEarningsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        white = ViewUtil.getColor(getContext(), R.color.white);
        handler = new Handler();
        decimalFormat = new DecimalFormat("0.00");
    }

    public void setOriginalValue(double originalValue) {
        this.originalValue = originalValue;
        addOriginalValueText();
    }

    public void setUpdatedValue(double updatedValue) {
        this.updatedValue = updatedValue;
    }

    private void addOriginalValueText() {
        String originalValueString = "$" + decimalFormat.format(originalValue);

        textSwitcher = new TextSwitcher(getContext());
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getContext());
                textView.setTypeface(Fonts.robotoMedium);
                textView.setTextSize(32);
                textView.setTextColor(white);
                return textView;
            }
        });
        textSwitcher.setCurrentText(originalValueString);

        textSwitcher.setInAnimation(getContext(), R.anim.slide_in_up);
        textSwitcher.setOutAnimation(getContext(), R.anim.slide_out_up);

        addView(textSwitcher);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textSwitcher.setText("$" + decimalFormat.format(updatedValue));
            }
        }, animationStartDelayTime);
    }

}
