package com.healthymedium.arc.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;


public class RoundedDrawable extends Drawable {

    private boolean drawStroke = false;
    private boolean drawFill = false;

    private Path path;
    private Paint fillPaint;
    private Paint strokePaint;
    private float strokeWidth;
    private int radius;

    // only used for drawing
    private Rect rect;
    private int height;
    private int width;

    public RoundedDrawable(){

        // initialize member variables
        rect = new Rect(0,0,0,0);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        width = bounds.width();
        height = bounds.height();

        // create a rect that's small enough that the stroke isn't cut off
        int offset = (int) (strokeWidth/2);
        path = getPath(bounds,radius);
        rect.set(offset,offset,width-offset,height-offset);
    }

    @Override
    public void draw(Canvas canvas) {
        if(drawFill){
            canvas.drawPath(path,fillPaint);
        }
        if(drawStroke) {
            canvas.drawPath(path,strokePaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        fillPaint.setAlpha(alpha);
        strokePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        fillPaint.setColorFilter(colorFilter);
        strokePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setFillColor(int color) {
        drawFill = color!=0;
        fillPaint.setColor(color);
    }

    public void setStrokeColor(int color) {
        drawStroke = color!=0;
        strokePaint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        strokeWidth = width;
        strokePaint.setStrokeWidth(width);
    }

    public void setStrokeDash(float length, float spacing) {
        strokePaint.setPathEffect(new DashPathEffect(new float[]{length,spacing}, 0));
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

}
