package com.healthymedium.test_suite.behaviors;

import com.healthymedium.arc.core.BaseFragment;

public class Behavior {

    protected boolean extensionAllowed = false;
    protected BaseFragment fragment;

    public Behavior(){

    }

    public boolean allowExtensions(){
        return extensionAllowed;
    }

    public void onOpened(BaseFragment fragment){
        this.fragment = fragment;
        //test.takeScreenshot(fragment.getView(),fragment.getSimpleTag()+" Opened");
    }

    public void onPoppedBack(BaseFragment fragment){
        this.fragment = fragment;
        //test.takeScreenshot(fragment.getView(),fragment.getSimpleTag()+" PoppedBack");
    }

}



