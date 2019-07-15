package com.healthymedium.arc.hints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.healthymedium.arc.utilities.ViewUtil;

public class HintHighlightTarget extends View{

    private final Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean pulsing = false;
    private HintPulse pulse;

    private ViewGroup parentView;
    private Bitmap bitmap;
    private View view;

    private int radius = 0;
    private int padding = 0;
    private int height;
    private int width;
    private int x,y;

    public HintHighlightTarget(Context context, ViewGroup parent, View view) {
        super(context);

        this.parentView = parent;
        this.view = view;
    }

    void process(){
        width = view.getWidth()+(2*padding);
        height = view.getHeight()+(2*padding);

        int locations[] = new int[2];
        view.getLocationOnScreen(locations);

        x = locations[0]-padding;
        y = locations[1]-padding;

        bitmap = Bitmap.createBitmap(parentView.getWidth(), parentView.getHeight(), Bitmap.Config.ARGB_8888);
        RectF rect = new RectF(x, y, x+width, y+height);

        Canvas canvas = new Canvas(bitmap);
        Path clipPath = new Path();

        if(height==width){
            clipPath.addCircle(x+width/2,y+height/2, radius, Path.Direction.CW);
        } else {
            clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        }

        canvas.clipPath(clipPath);

        parentView.draw(canvas);

        if(pulsing){
            pulse.setRadius(ViewUtil.pxToDp(radius));
            pulse.process();
            pulse.start();
        }
    }

    public void setRadius(int dp) {
        radius = ViewUtil.dpToPx(dp);
    }

    public void setPulsing() {
        pulse = new HintPulse(getContext(),view);
        pulsing = true;
    }

    public void setPadding(int dp) {
        padding = ViewUtil.dpToPx(dp);
    }

    public View getView(){
        return view;
    }

    public HintPulse getPulse(){
        return pulse;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0,0, basePaint);
    }

    public void cleanup(){
        if(bitmap!=null) {
            bitmap.recycle();
        }
        if(pulse!=null){
            pulse.cleanup();
        }
    }

    public boolean wasTouched(MotionEvent event){
        int touch_x = (int) event.getX();
        int touch_y = (int) event.getY();
        if(touch_x<x || touch_x>(x+width)){
            return false;
        }
        if(touch_y<y || touch_y>(y+height)){
            return false;
        }
        return true;
    }

}
