package com.healthymedium.arc.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class ChipLinearLayout extends LinearLayout {

    ChipDrawable background;

    public ChipLinearLayout(Context context) {
        super(context);
        init(null,0);
    }

    public ChipLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChipLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        background = new ChipDrawable();
        setBackground(background);

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ChipLinearLayout, defStyle, 0);

        int fillColor = typedArray.getColor(R.styleable.ChipLinearLayout_fillColor,0);
        int strokeColor = typedArray.getColor(R.styleable.ChipLinearLayout_strokeColor,0);
        float dashLength = typedArray.getDimension(R.styleable.ChipLinearLayout_dashLength,0);
        float dashSpacing = typedArray.getDimension(R.styleable.ChipLinearLayout_dashSpacing,0);
        int strokeWidth = (int) typedArray.getDimension(R.styleable.ChipLinearLayout_strokeWidth,0);

        int gradientEnum = (int) typedArray.getInt(R.styleable.ChipLinearLayout_gradient,-1);
        int gradientColor0 = (int) typedArray.getColor(R.styleable.ChipLinearLayout_gradientColor0,0);
        int gradientColor1 = (int) typedArray.getColor(R.styleable.ChipLinearLayout_gradientColor1,0);

        typedArray.recycle();


        if(fillColor!=0){
            background.setFillColor(fillColor);
        }
        if(strokeColor!=0) {
            background.setStrokeColor(strokeColor);
        }
        background.setStrokeWidth(strokeWidth);

        if(dashLength!=0 && dashSpacing!=0){
            background.setStrokeDash(dashLength,dashSpacing);
        }

        if(gradientEnum!=-1 && gradientColor0!=0 && gradientColor1!=0){
            background.setGradient(gradientEnum,gradientColor0,gradientColor1);
        }
    }

    public void setFillColor(@ColorRes int color) {
        background.setFillColor(ViewUtil.getColor(color));
    }

    public void setStrokeColor(@ColorRes int color) {
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

    public void setHorizontalGradient(@ColorRes int colorLeft, @ColorRes int colorRight) {
        int left = ViewUtil.getColor(colorLeft);
        int right = ViewUtil.getColor(colorRight);
        background.setGradient(SimpleGradient.LINEAR_HORIZONTAL,left,right);
    }

    public void setVerticalGradient(@ColorRes int colorTop, @ColorRes int colorBottom) {
        int top = ViewUtil.getColor(colorTop);
        int bottom = ViewUtil.getColor(colorBottom);
        background.setGradient(SimpleGradient.LINEAR_VERTICAL,top,bottom);
    }

    public void refresh(){
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(background.getOutlineProvider());
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return background.getOutlineProvider();
    }

}
