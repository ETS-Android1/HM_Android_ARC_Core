package com.healthymedium.arc.custom.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class ChipButton extends LinearLayout {

    private ValueAnimator enableAnimator;

    LayerDrawable background;
    ChipDrawable bottom;
    ChipDrawable gradient;
    FadingDrawable top;

    float elevation;

    int defaultWidth = ViewUtil.dpToPx(216);
    int defaultHeight = ViewUtil.dpToPx(48);

    public ChipButton(Context context) {
        super(context);
        init(null,0);
    }

    public ChipButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChipButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        Context context = getContext();
        setGravity(Gravity.CENTER);

        enableAnimator = new ValueAnimator();
        enableAnimator.addUpdateListener(enableListener);

        bottom = new ChipDrawable();
        gradient = new ChipDrawable();
        top = new FadingDrawable();

        background = new LayerDrawable(new Drawable[]{bottom,gradient,top});
        setBackground(background);

        int color = ViewUtil.getColor(context,R.color.primary);

        bottom.setFillColor(color);

        int white = Color.argb(64,255,255,255);
        int black = Color.argb(64,0,0,0);
        gradient.setGradient(SimpleGradient.LINEAR_VERTICAL,white,black);

        top.setStrokeWidth(ViewUtil.dpToPx(16));
        top.setStrokeColor(color);
        top.setFillColor(color);

        elevation = getElevation();
        setOnTouchListener(touchListener);
    }

    protected void setColor(@ColorInt int color) {
        bottom.setFillColor(color);
        top.setStrokeColor(color);
        top.setFillColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(defaultWidth, widthSize);
        } else {
            width = defaultWidth;
        }

        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(defaultHeight, heightSize);
        } else {
            height = defaultHeight;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(bottom.getOutlineProvider());
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return bottom.getOutlineProvider();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            gradient.setAlpha(255);
            top.setAlpha(255);
            setElevation(elevation);
            setAlpha(1.0f);
        } else {
            gradient.setAlpha(0);
            top.setAlpha(0);
            setElevation(0);
            setAlpha(0.5f);
        }
    }

    public void setEnabled(boolean enabled, boolean animate) {
        if(!animate){
            setEnabled(enabled);
            return;
        }
        super.setEnabled(enabled);
        int v0 = enabled ? 0:255;
        int v1 = enabled ? 255:0;
        enableAnimator.setIntValues(v0,v1);
        enableAnimator.start();
    }

    ValueAnimator.AnimatorUpdateListener enableListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int value = (int) animation.getAnimatedValue();
            float percentage  = value/255f;
            setAlpha((percentage/2f)+0.5f); // scale between 0.5 and 1.0
            gradient.setAlpha(value);
            top.setAlpha(value);
            setElevation(percentage*elevation);
            invalidate();
        }
    };

    OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    setElevation(elevation);
                    gradient.setAlpha(255);
                    top.setAlpha(255);
                    invalidate();

                    float x = event.getX();
                    float y = event.getY();
                    int w = v.getWidth();
                    int h = v.getHeight();

                    if(x>0 && x<w && y>0 && y<h){
                        callOnClick();
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    setElevation(0);
                    gradient.setAlpha(0);
                    top.setAlpha(0);
                    invalidate();
                    break;
            }
            return true;
        }
    };

    public class FadingDrawable extends ChipDrawable {

        float numberOfPasses = 20;

        @Override
        public void draw(Canvas canvas) {
            if(path==null){
                return;
            }
            if(drawFill){
                canvas.drawPath(path,fillPaint);
            }
            if(drawStroke) {
                float maxWidth = strokeWidth;

                for (float i = 0; i <= numberOfPasses; i++){
                    int alpha = (int) (i / numberOfPasses * 255f);
                    float width = maxWidth * (1 - i / numberOfPasses);
                    strokePaint.setAlpha(alpha);
                    strokePaint.setStrokeWidth(width);
                    canvas.drawPath(path, strokePaint);
                }
            }
        }
    }

}
