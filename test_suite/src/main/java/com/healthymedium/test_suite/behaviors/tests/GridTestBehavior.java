package com.healthymedium.test_suite.behaviors.tests;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;

import static androidx.test.espresso.action.ViewActions.click;

import com.healthymedium.arc.library.R;


public class GridTestBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        try {
            UI.click(R.id.image11);
            UI.sleep(500);
            UI.click(R.id.image22);
            UI.sleep(500);
            UI.click(R.id.image33);
            UI.sleep(500);
        } catch (Exception e) {

        }

        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "clicked");
        UI.sleep(2000);
    }

}
