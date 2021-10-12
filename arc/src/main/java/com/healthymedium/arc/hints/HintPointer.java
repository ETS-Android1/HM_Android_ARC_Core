package com.healthymedium.arc.hints;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;

import android.util.Log;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;


public class HintPointer extends LinearLayout {

    private String tag;

    private static final int SHOW_ABOVE = 1;
    private static final int SHOW_CENTER = 0;
    private static final int SHOW_BELOW = -1;

    int dp4 = ViewUtil.dpToPx(4);
    int dp8 = ViewUtil.dpToPx(8);
    int dp12 = ViewUtil.dpToPx(12);
    int dp16 = ViewUtil.dpToPx(16);
    int dp44 = ViewUtil.dpToPx(44);

    private int x,y;

    private int left;
    private int top;
    private int right;
    private int bottom;

    private int pointerX;

    private int height;
    private int width;

    private boolean showArrow;
    private int showOrientation;

    private View border;
    private View spacer;
    private AppCompatTextView textView;
    private TextView textViewButton;

    private ViewGroup parent;
    private View target;

    private Paint fillPaint;
    private Paint strokePaint;
    private int radius;

    private boolean dismissing = false;

    private HintHighlighter shadow;

    public HintPointer(Activity activity, View view) {
        super(activity);
        parent = (ViewGroup) activity.getWindow().getDecorView();
        this.showOrientation = SHOW_CENTER;
        this.showArrow = false;
        this.target = view;
        init();
    }

    public HintPointer(Activity activity, View view, boolean showShadow) {
        super(activity);
        parent = (ViewGroup) activity.getWindow().getDecorView();

        if(showShadow) {
            shadow = new HintHighlighter(activity);
        }

        this.showOrientation = SHOW_CENTER;
        this.showArrow = false;
        this.target = view;
        init();
    }

    public HintPointer(Activity activity, View view, boolean showArrow, boolean showAbove) {
        super(activity);
        parent = (ViewGroup) activity.getWindow().getDecorView();

        this.showOrientation = showAbove?SHOW_ABOVE:SHOW_BELOW;
        this.showArrow = showArrow;
        this.target = view;
        init();
    }

    public HintPointer(Activity activity, View view, boolean showArrow, boolean showAbove, boolean showShadow) {
        super(activity);
        parent = (ViewGroup) activity.getWindow().getDecorView();

        if(showShadow) {
            shadow = new HintHighlighter(activity);
        }

        this.showOrientation = showAbove?SHOW_ABOVE:SHOW_BELOW;
        this.showArrow = showArrow;
        this.target = view;
        init();
    }
    private void init() {
        setWillNotDraw(false);

        tag =  getClass().getSimpleName();
        target.addOnAttachStateChangeListener(attachStateChangeListener);

        radius = ViewUtil.dpToPx(16); // default to 16dp radius

        setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp44,0,dp44,0);
        setLayoutParams(layoutParams);

        textView = new AppCompatTextView(getContext());
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView,ViewUtil.dpToPx(10),ViewUtil.dpToPx(18),1, TypedValue.COMPLEX_UNIT_PX);

        textView.setPadding(dp16,dp16,dp16,dp16);
        textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(Fonts.roboto);
        textView.setTextColor(getResources().getColor(R.color.black));
        addView(textView);

        border = new View(getContext());
        border.setBackgroundColor(ViewUtil.getColor(R.color.hintDark));
        LayoutParams borderParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewUtil.dpToPx(2));
        borderParams.setMargins(dp4,0,dp4,0);
        border.setLayoutParams(borderParams);
        border.setVisibility(GONE);
        addView(border);

        textViewButton = new TextView(getContext());
        textViewButton.setPadding(dp16,dp8,dp16,dp16);
        textViewButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textViewButton.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textViewButton.setTypeface(Fonts.robotoBold);
        ViewUtil.underlineTextView(textViewButton);
        textViewButton.setVisibility(GONE);
        textViewButton.setTextColor(getResources().getColor(R.color.black));
        addView(textViewButton);

        spacer = new View(getContext());
        spacer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dp16));
        // don't add yet

        if(showArrow){
            if(showOrientation==SHOW_ABOVE){
                addView(spacer);
            } else {
                addView(spacer,0);
            }
        }

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(ViewUtil.getColor(R.color.hintLight));

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(dp4);
        strokePaint.setColor(ViewUtil.getColor(R.color.hintDark));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        height = canvas.getHeight();
        width = canvas.getWidth();

        int locations[] = new int[2];
        target.getLocationOnScreen(locations);

        x = locations[0];
        y = locations[1];

        left = dp44;
        top = 0;
        right = dp44;
        bottom = 0;

        int pointerMin = radius+dp16+dp4;

        pointerX = x+(target.getWidth()/2);

        if(pointerX < (dp44+pointerMin)){
            left = 0;
            right = 2*dp44;
            if(pointerX < pointerMin){
                pointerX = pointerMin;
            }
        } else if(pointerX > (width+dp44-pointerMin)){
            left = 2*dp44;
            right = 0;
            pointerX -= 2*dp44;
            if(pointerX > (width-pointerMin)){
                pointerX = (width-pointerMin);
            }
        } else {
            pointerX -= dp44;
        }

        // adjust top margin for canvas size
        switch (showOrientation){
            case SHOW_ABOVE:
                top = y-height;
                break;
            case SHOW_CENTER:
                top = y+((target.getHeight()-height)/2);
                break;
            case SHOW_BELOW:
                top = y+target.getHeight();
                break;
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.setMargins(left,top,right,bottom);
        setLayoutParams(params);

        // create a rect that's small enough that the stroke isn't cut off
        Rect rect = new Rect(dp4,dp4,width-dp4,height-dp4);

        Path path = getPath(pointerX,rect,radius,showArrow,showOrientation);

        canvas.drawPath(path,fillPaint);
        canvas.drawPath(path,strokePaint);
    }

    private Path getPath(int pointerX, Rect rect, int radius, boolean showArrow, int showOrientation) {
        int pointerSize = ViewUtil.dpToPx(16);

        int top = rect.top;
        int bottom = rect.bottom;

        if(showArrow){
            switch (showOrientation){
                case SHOW_ABOVE:
                    bottom = rect.bottom-pointerSize;
                    break;
                case SHOW_BELOW:
                    top = rect.top+pointerSize;
                    break;
            }
        }

        Path path = new Path();

        path.moveTo(rect.left + radius, top);

        // top pointer
        if(showArrow && (showOrientation==SHOW_BELOW)) {
            path.lineTo(pointerX - pointerSize, top);
            path.lineTo(pointerX, rect.top);
            path.lineTo(pointerX + pointerSize, top);
        }

        // top line
        path.lineTo(rect.right - radius, top);

        // top right corner
        RectF topRightRect = new RectF(rect.right - radius,top,rect.right,top+radius);
        path.arcTo(topRightRect, 270F, 90F, false);

        // right line
        path.lineTo(rect.right, bottom - radius);

        // bottom right corner
        RectF bottomRightRect = new RectF(rect.right - radius,bottom - radius,rect.right,bottom);
        path.arcTo(bottomRightRect, 0F, 90F, false);

        // bottom pointer
        if(showArrow && (showOrientation==SHOW_ABOVE)) {
            path.lineTo(pointerX + pointerSize, bottom);
            path.lineTo(pointerX, rect.bottom);
            path.lineTo(pointerX - pointerSize, bottom);
        }

        // bottom line
        path.lineTo(rect.left + radius, bottom);

        // bottom left corner
        RectF bottomLeftRect = new RectF(rect.left,bottom - radius,rect.left+radius,bottom);
        path.arcTo(bottomLeftRect, 90F, 90F, false);

        // left side
        path.lineTo(rect.left, top + radius);

        // top left corner
        RectF topLeftRect = new RectF(rect.left,top,rect.left+radius,top+radius);
        path.arcTo(topLeftRect, 180F, 90F, false);

        path.close();

        return path;
    }


    public void addButton(String text, final OnClickListener listener) {
        textViewButton.setText(Html.fromHtml(text));
        textViewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                Log.d(getTag(),"onClick");
                if(listener!=null) {
                    listener.onClick(v);
                }
            }
        });
        border.setVisibility(VISIBLE);
        textViewButton.setVisibility(VISIBLE);

        if(!hasLogDescription()){
            String desc = textViewButton.getText().toString();
            setLogDescription(desc);
        }
    }

    public void setText(String text) {
        textView.setText(Html.fromHtml(text));
        String desc = textView.getText().toString();
        setLogDescription(desc);
    }

    public void hideText() {
        border.setVisibility(GONE);
        textView.setVisibility(GONE);
        textViewButton.setPadding(dp16, ViewUtil.dpToPx(18), dp16, ViewUtil.dpToPx(22));
    }

    public void setRadius(int dp) {
        radius = ViewUtil.dpToPx(dp);
    }

    public HintHighlighter getShadow() {
        return shadow;
    }

    public void show() {
        Log.d(getTag(),"onShow");

        if(getParent()!=null) {
           return; // single use only
        }
        textViewButton.setEnabled(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAlpha(0.0f);
                if(HintPointer.this.getParent()!=null){
                    ((ViewGroup)HintPointer.this.getParent()).removeView(HintPointer.this);
                }
                parent.addView(HintPointer.this);
                HintPointer.this.animate()
                        .alpha(1.0f)
                        .setDuration(400);
            }
        },100);
        if(shadow!=null) {
            shadow.show();
        }
    }

    public void dismiss() {
        Log.d(getTag(),"onDismiss");

        if (dismissing) {
            return;
        }
        dismissing = true;

        if(target!=null) {
            target.removeOnAttachStateChangeListener(attachStateChangeListener);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                parent.removeView(HintPointer.this);
                dismissing = false;
            }
        },500);

        HintPointer.this.animate()
                .alpha(0.0f)
                .setDuration(400);

        if(shadow!=null) {
            shadow.dismiss();
        }
    }

    public String getTag() {
        return tag;
    }

    private void setLogDescription(String desc) {
        if(desc.length() > 10){
            desc = desc.substring(0,10) + "...";
        }
        tag =  getClass().getSimpleName() + "(" + desc + ")";
    }

    private boolean hasLogDescription() {
        return tag.contains("(");
    }

    OnAttachStateChangeListener attachStateChangeListener = new OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            Log.d(getTag(),"onViewAttachedToWindow");
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            Log.d(getTag(),"onViewDetachedFromWindow");
            if(!dismissing) {
                dismiss();
            }
        }
    };

}
