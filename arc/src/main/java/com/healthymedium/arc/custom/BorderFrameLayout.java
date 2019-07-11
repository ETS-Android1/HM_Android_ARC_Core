package com.healthymedium.arc.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;


public class BorderFrameLayout extends FrameLayout {

    private Paint fillPaint;
    private Paint strokePaint;
    private int strokeWidth;
    private int radius;

    // only used for drawing
    private Rect rect;
    private int height;
    private int width;

    public BorderFrameLayout(Context context) {
        super(context);
        init(null,0);
    }

    public BorderFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BorderFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setWillNotDraw(false);

        // initialize member variables
        rect = new Rect(0,0,0,0);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);

        //
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BorderFrameLayout, defStyle, 0);

        int fillColor = typedArray.getColor(R.styleable.BorderFrameLayout_fillColor,0);
        int strokeColor = typedArray.getColor(R.styleable.BorderFrameLayout_strokeColor,0);
        float dashLength = typedArray.getDimension(R.styleable.BorderFrameLayout_dashLength,0);
        float dashSpacing = typedArray.getDimension(R.styleable.BorderFrameLayout_dashSpacing,0);
        strokeWidth = (int) typedArray.getDimension(R.styleable.BorderFrameLayout_strokeWidth,0);
        radius = (int) typedArray.getDimension(R.styleable.BorderFrameLayout_radius,0);

        typedArray.recycle();

        if(fillColor!=0){
            fillPaint.setColor(fillColor);
        }
        if(strokeColor!=0) {
            strokePaint.setColor(strokeColor);
        }

        strokePaint.setStrokeWidth(strokeWidth);

        if(dashLength!=0 && dashSpacing!=0){
            strokePaint.setPathEffect(new DashPathEffect(new float[]{dashLength,dashSpacing}, 0));
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // create a rect that's small enough that the stroke isn't cut off
        int offset = (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);

        Path path = getPath(rect,radius);
        canvas.drawPath(path,fillPaint);
        canvas.drawPath(path,strokePaint);
    }

    private Path getPath(Rect rect, int radius) {

        Path path = new Path();

        path.moveTo(rect.left + radius, rect.top);

        // top line
        path.lineTo(rect.right - radius, rect.top);

        // top right corner
        RectF topRightRect = new RectF(rect.right - radius,rect.top,rect.right,rect.top+radius);
        path.arcTo(topRightRect, 270F, 90F, false);

        // right line
        path.lineTo(rect.right, rect.bottom - radius);

        // bottom right corner
        RectF bottomRightRect = new RectF(rect.right - radius,rect.bottom - radius,rect.right,rect.bottom);
        path.arcTo(bottomRightRect, 0F, 90F, false);

        // bottom line
        path.lineTo(rect.left + radius, rect.bottom);

        // bottom left corner
        RectF bottomLeftRect = new RectF(rect.left,rect.bottom - radius,rect.left+radius,rect.bottom);
        path.arcTo(bottomLeftRect, 90F, 90F, false);

        // left side
        path.lineTo(rect.left, rect.top + radius);

        // top left corner
        RectF topLeftRect = new RectF(rect.left,rect.top,rect.left+radius,rect.top+radius);
        path.arcTo(topLeftRect, 180F, 90F, false);

        path.close();

        return path;
    }

    public void setRadius(int dp) {
        radius = ViewUtil.dpToPx(dp);
    }

    public void setFillColor(@ColorRes  int color) {
        fillPaint.setColor(ViewUtil.getColor(color));
    }

    public void setStrokeColor(@ColorRes  int color) {
        strokePaint.setColor(ViewUtil.getColor(color));
    }

    public void setStrokeWidth(int dp) {
        strokeWidth = ViewUtil.dpToPx(dp);
        strokePaint.setStrokeWidth(strokeWidth);
    }

    public void setStrokeDash(int dpLength, int dpSpacing) {
        int length = ViewUtil.dpToPx(dpLength);
        int spacing = ViewUtil.dpToPx(dpSpacing);
        strokePaint.setPathEffect(new DashPathEffect(new float[]{length,spacing}, 0));
    }

    public void refresh(){
        invalidate();
    }

}
