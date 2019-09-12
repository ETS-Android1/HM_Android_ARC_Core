package com.healthymedium.arc.hints;

import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class HintHighlighter extends FrameLayout {

    private static final int backgroundColor = ViewUtil.getColor(R.color.shadow);
    private List<HintHighlightTarget> targets;
    private ViewGroup parent;

    public HintHighlighter(Activity activity) {
        super(activity);
        super.setOnTouchListener(touchListener);
        super.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        targets = new ArrayList<>();
        init();
    }

    public HintHighlighter(Activity activity, View view) {
        super(activity);
        super.setOnTouchListener(touchListener);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        targets = new ArrayList<>();
        targets.add(new HintHighlightTarget(getContext(),view,targetListener));
        init();
    }

    public HintHighlighter(Activity activity, List<View> views) {
        super(activity);
        super.setOnTouchListener(touchListener);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        for(View view : views){
            targets.add(new HintHighlightTarget(getContext(),view,targetListener));
        }
        init();
    }

    private void init(){
        setBackgroundColor(backgroundColor);
    }

    public HintHighlightTarget getTarget(View view) {
        for(HintHighlightTarget target : targets){
            if(target.getView().equals(view)){
                return target;
            }
        }
        return null;
    }

    public void addPulsingTarget(View view) {
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view,targetListener);
        target.setPulsing(getContext(),0);
        targets.add(target);
    }

    public void addPulsingTarget(View view, int dpRaduis) {
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view,targetListener);
        target.setRadius(dpRaduis);
        target.setPulsing(getContext(),0);
        targets.add(target);
    }

    public void addTarget(View view) {
        targets.add(new HintHighlightTarget(getContext(),view,targetListener));
    }

    public void addTarget(View view, int dpRaduis) {
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view,targetListener);
        target.setRadius(dpRaduis);
        targets.add(target);
    }

    public void addTarget(View view, int dpRaduis, int dpPadding) {
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view, targetListener);
        target.setRadius(dpRaduis);
        target.setPadding(dpPadding);
        targets.add(target);
    }

    public void show() {
        setAlpha(0.0f);

        parent.addView(HintHighlighter.this);
        for(HintHighlightTarget target : targets) {
            if(target.getPulse()!=null){
                addView(target.getPulse());
            }
            addView(target);
        }

        HintHighlighter.this.animate()
                .alpha(1.0f)
                .setDuration(400);
    }

    public void dismiss() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                parent.removeView(HintHighlighter.this);
                for(HintHighlightTarget target : targets) {
                    target.cleanup();
                }
            }
        },500);

        HintHighlighter.this.animate()
                .alpha(0.0f)
                .setDuration(400);
    }

    OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            for(HintHighlightTarget target : targets){
                if(target.wasTouched(event)){
                    return false;
                }
            }
            return true;
        }
    };

    HintHighlightTarget.Listener targetListener = new HintHighlightTarget.Listener() {
        @Override
        public void onLayout(HintHighlightTarget target) {
            invalidate();
        }
    };

}
