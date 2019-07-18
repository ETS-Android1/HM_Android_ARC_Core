package com.healthymedium.arc.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
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
    private Gradient gradient;

    private Path path;
    private Paint fillPaint;
    private Paint strokePaint;
    private float strokeWidth;
    private int radiusTopLeft;
    private int radiusTopRight;
    private int radiusBottomLeft;
    private int radiusBottonRight;

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
        rect.set(offset,offset,width-offset,height-offset);

        int maxRadius = Math.min(rect.width()/2, rect.height()/2);

        if(radiusTopLeft > maxRadius) {
            radiusTopLeft = maxRadius;
        }
        if(radiusTopRight > maxRadius) {
            radiusTopRight = maxRadius;
        }
        if(radiusBottomLeft > maxRadius) {
            radiusBottomLeft = maxRadius;
        }
        if(radiusBottonRight > maxRadius) {
            radiusBottonRight = maxRadius;
        }

        path = getPath(rect);

        if(gradient!=null){
            fillPaint.setShader(gradient.getShader(width,height));
        }
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
        radiusTopLeft = radius;
        radiusTopRight = radius;
        radiusBottomLeft = radius;
        radiusBottonRight = radius;
    }

    public void setRadius(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        radiusTopLeft = topLeft;
        radiusTopRight = topRight;
        radiusBottomLeft = bottomLeft;
        radiusBottonRight = bottomRight;
    }

    public void setFillColor(int color) {
        drawFill = color!=0;
        fillPaint.setColor(color);
    }

    public void setFillShader(Shader shader) {
        drawFill = true;
        fillPaint.setShader(shader);
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

    public void setHorizontalGradient(int colorLeft, int colorRight) {
        drawFill = true;
        gradient = Gradient.LINEAR_HORIZONTAL;
        gradient.setColor0(colorLeft);
        gradient.setColor1(colorRight);
        gradient.setTileMode(Shader.TileMode.CLAMP);
    }

    public void setVerticalGradient(int colorTop, int colorBottom) {
        drawFill = true;
        gradient = Gradient.LINEAR_VERTICAL;
        gradient.setColor0(colorTop);
        gradient.setColor1(colorBottom);
        gradient.setTileMode(Shader.TileMode.CLAMP);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private Path getPath(Rect rect) {

        Path path = new Path();

        path.moveTo(rect.left + radiusTopLeft, rect.top);

        // top line
        path.lineTo(rect.right - radiusTopRight, rect.top);

        // top right corner
        RectF topRightRect = new RectF(rect.right - radiusTopRight,rect.top,rect.right,rect.top+radiusTopRight);
        path.arcTo(topRightRect, 270F, 90F, false);

        // right line
        path.lineTo(rect.right, rect.bottom - radiusBottonRight);

        // bottom right corner
        RectF bottomRightRect = new RectF(rect.right - radiusBottonRight,rect.bottom - radiusBottonRight,rect.right,rect.bottom);
        path.arcTo(bottomRightRect, 0F, 90F, false);

        // bottom line
        path.lineTo(rect.left + radiusBottomLeft, rect.bottom);

        // bottom left corner
        RectF bottomLeftRect = new RectF(rect.left,rect.bottom - radiusBottomLeft,rect.left+radiusBottomLeft,rect.bottom);
        path.arcTo(bottomLeftRect, 90F, 90F, false);

        // left side
        path.lineTo(rect.left, rect.top + radiusTopLeft);

        // top left corner
        RectF topLeftRect = new RectF(rect.left,rect.top,rect.left+radiusTopLeft,rect.top+radiusTopLeft);
        path.arcTo(topLeftRect, 180F, 90F, false);

        path.close();

        return path;
    }


    public enum Gradient{
        LINEAR_HORIZONTAL(0),
        LINEAR_VERTICAL(1);

        int id;
        int color0;
        int color1;
        Shader.TileMode tileMode;

        Gradient(int enumeratedValue){
            id = enumeratedValue;
        }

        Shader getShader(int width, int height){
            switch (this){
                case LINEAR_VERTICAL:
                    return new LinearGradient(0, 0, 0, height, color0, color1, tileMode);
                case LINEAR_HORIZONTAL:
                    return new LinearGradient(0,0,width,0,color0,color1,tileMode);
            }
            return null;
        }

        void setColor0(int color){
            this.color0 = color;
        }

        void setColor1(int color){
            this.color1 = color;
        }

        void setTileMode(Shader.TileMode tileMode){
            this.tileMode = tileMode;
        }

        public static Gradient fromId(int id){
            for (Gradient gradient : values()) {
                if (gradient.id == id) return gradient;
            }
            return null;
        }
    }



}
