package com.healthymedium.test_suite.behaviors.questions;

import androidx.test.espresso.ViewAction;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.test_suite.utilities.ViewActions;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

import static androidx.test.espresso.action.ViewActions.click;


public class QuestionTimeBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        ViewAction setTime = ViewActions.setTime(8,0);
        UI.getTimePicker().perform(setTime);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "setTime");
        UI.sleep(500);
        UI.click(R.id.buttonNext);
    }

}
