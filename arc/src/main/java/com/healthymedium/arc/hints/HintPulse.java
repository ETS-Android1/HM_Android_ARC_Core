package com.healthymedium.arc.hints;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class HintPulse extends View{

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ObjectAnimator animator;
    private Bitmap bitmap;
    private View view;

    private int radius = 0;
    private int height;
    private int width;
    private int x,y;

    public HintPulse(Context context, View view) {
        super(context);
        this.view = view;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ViewUtil.dpToPx(2));
        paint.setColor(ViewUtil.getColor(R.color.hintDark));
    }

    void process(){
        width = view.getWidth();
        height = view.getHeight();

        int locations[] = new int[2];
        view.getLocationOnScreen(locations);

        x = locations[0];
        y = locations[1];

        setPivotX(x+(width/2));
        setPivotY(y+(height/2));

        bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        RectF rect = new RectF(0,0, width, height);

        Canvas canvas = new Canvas(bitmap);

        if(height==width){
            canvas.drawCircle(width/2,height/2, radius, paint);
        } else {
            canvas.drawRoundRect(rect, radius, radius, paint);
        }

    }

    public void setRadius(int dp) {
        radius = ViewUtil.dpToPx(dp);
    }

    public View getView(){
        return view;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, x,y, paint);
    }

    public void cleanup(){
        if(bitmap!=null) {
            bitmap.recycle();
        }
    }

    public void start(){

        float scaleX;
        float scaleY;

        if(width>height){
            scaleX = 1.25f;
            scaleY = (height+(0.25f*width))/height;
        } else {
            scaleY = 1.25f;
            scaleX = (width+(0.25f*height))/width;
        }

        animator = ObjectAnimator.ofPropertyValuesHolder(
                this,
                PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_X, scaleX),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleY));
        animator.setDuration(2000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animation.start();
                    }
                }, 500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public void stop(){
        if(animator!=null){
            animator.cancel();
        }
    }

}
