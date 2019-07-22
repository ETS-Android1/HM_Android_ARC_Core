package com.healthymedium.arc.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.List;

import static com.healthymedium.arc.custom.RoundedDrawable.Gradient.LINEAR_HORIZONTAL;
import static com.healthymedium.arc.custom.RoundedDrawable.Gradient.LINEAR_VERTICAL;

public class StudyProgressView extends LinearLayout {

    int currentVisit;
    int visitCount;

    int dp4;
    int dp8;
    int dp32;
    int dp42;

    public StudyProgressView(Context context) {
        super(context);
        init(null,0);
    }

    public StudyProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StudyProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        Context context = getContext();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        // init woth dummy values
        currentVisit = 4;
        visitCount = 12;

        if(!isInEditMode()){
            ParticipantState state = Study.getParticipant().getState();
            visitCount = state.visits.size();

            // offset because internal data is indexed 0
            // and this view uses indexed 1
            currentVisit = state.currentVisit+1;
        }

        int dp1 = ViewUtil.dpToPx(1);
        int dp2 = ViewUtil.dpToPx(2);

        dp4 = ViewUtil.dpToPx(4);
        dp8 = ViewUtil.dpToPx(8);
        dp32 = ViewUtil.dpToPx(32);
        dp42 = ViewUtil.dpToPx(42);

        int color = ViewUtil.getColor(getContext(),R.color.secondaryAccent);

        for(int i=0;i<visitCount;i++){
            RoundedDrawable drawable = new RoundedDrawable();
            drawable.setRadius(dp4);
            drawable.setStrokeColor(color);
            drawable.setStrokeWidth(dp1);

            if(i<=currentVisit){
                drawable.setFillColor(color);
            }

            View view = new View(context);
            view.setBackground(drawable);

            LayoutParams params;
            if(i==currentVisit){
                params = new LayoutParams(dp8,dp42);
            } else {
                params = new LayoutParams(dp8,dp32);
            }

            if(i==0){
                params.setMargins(0,0,dp4,0);
            } else if(i==visitCount-1){
                params.setMargins(dp4,0,0,0);
            } else {
                params.setMargins(dp4,0,dp4,0);
            }

            view.setLayoutParams(params);
            addView(view);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int blockWidth = ((width-((visitCount-1)*dp8))/visitCount);

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View view = getChildAt(i);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.width = blockWidth;
            view.setLayoutParams(params);
        }

        setMeasuredDimension(width,dp42);

    }

    public void refresh(){
        invalidate();
    }

}
