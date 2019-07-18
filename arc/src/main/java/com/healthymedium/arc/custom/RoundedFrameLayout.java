package com.healthymedium.arc.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;


public class RoundedFrameLayout extends FrameLayout {

    RoundedDrawable background;

    public RoundedFrameLayout(Context context) {
        super(context);
        init(null,0);
    }

    public RoundedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RoundedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        background = new RoundedDrawable();
        setBackground(background);

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedFrameLayout, defStyle, 0);

        int fillColor = typedArray.getColor(R.styleable.RoundedFrameLayout_fillColor,0);
        int strokeColor = typedArray.getColor(R.styleable.RoundedFrameLayout_strokeColor,0);
        float dashLength = typedArray.getDimension(R.styleable.RoundedFrameLayout_dashLength,0);
        float dashSpacing = typedArray.getDimension(R.styleable.RoundedFrameLayout_dashSpacing,0);
        int strokeWidth = (int) typedArray.getDimension(R.styleable.RoundedFrameLayout_strokeWidth,0);
        int radius = (int) typedArray.getDimension(R.styleable.RoundedFrameLayout_radius,0);

        typedArray.recycle();


        if(fillColor!=0){
            background.setFillColor(fillColor);
        }
        if(strokeColor!=0) {
            background.setStrokeColor(strokeColor);
        }
        background.setStrokeWidth(strokeWidth);
        background.setRadius(radius);

        if(dashLength!=0 && dashSpacing!=0){
            background.setStrokeDash(dashLength,dashSpacing);
        }
    }

    public void setRadius(int dp) {
        background.setRadius(ViewUtil.dpToPx(dp));
    }

    public void setFillColor(@ColorRes  int color) {
        background.setFillColor(ViewUtil.getColor(color));
    }

    public void setStrokeColor(@ColorRes  int color) {
        background.setStrokeColor(ViewUtil.getColor(color));
    }

    public void setStrokeWidth(int dp) {
        background.setStrokeWidth(ViewUtil.dpToPx(dp));
    }

    public void setStrokeDash(int dpLength, int dpSpacing) {
        int length = ViewUtil.dpToPx(dpLength);
        int spacing = ViewUtil.dpToPx(dpSpacing);
        background.setStrokeDash(length,spacing);
    }

    public void refresh(){
        invalidate();
    }

}
