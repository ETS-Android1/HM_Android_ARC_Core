package com.healthymedium.sample;

import android.transition.Slide;
import android.view.Gravity;

import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.StudyStateMachine;

public class SampleStateMachine extends StudyStateMachine {

    Slide slideIn;
    Slide slideOut;

    public SampleStateMachine() {
        slideIn = new Slide(Gravity.RIGHT);
        slideIn.setStartDelay(200);

        slideOut = new Slide(Gravity.LEFT);
        slideOut.setDuration(200);
    }

    private void enableTransition(PathSegment segment){
        int size = segment.fragments.size();
        for(int i=0;i<size;i++){
            segment.fragments.get(i).setEnterTransition(slideIn);
            segment.fragments.get(i).setEnterTransition(slideOut);
        }
    }

    @Override
    public void decidePath(){

    }

    @Override
    protected void setupPath(){
        addContextSurvey();
    }

}
