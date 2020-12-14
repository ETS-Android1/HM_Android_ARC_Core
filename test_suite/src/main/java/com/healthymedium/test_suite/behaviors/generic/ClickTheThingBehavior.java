package com.healthymedium.test_suite.behaviors.generic;

import androidx.annotation.IdRes;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

public class ClickTheThingBehavior extends Behavior {

    @IdRes int id;
    String actionName = null;
    long ms;

    public ClickTheThingBehavior(@IdRes int id){
        this.id = id;
        this.ms = 0;
        this.actionName = null;
    }
    public ClickTheThingBehavior(@IdRes int id, long ms){
        this.id = id;
        this.ms = ms;
        this.actionName = null;
    }

    public ClickTheThingBehavior(@IdRes int id, long ms, String name){
        this.id = id;
        this.ms = ms;
        this.actionName = name;

    }


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(ms);
        if(actionName != null && !actionName.trim().isEmpty()) {
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), actionName);
        }
        UI.click(id);
    }

}
