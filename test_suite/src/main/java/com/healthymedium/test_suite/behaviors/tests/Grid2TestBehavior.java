package com.healthymedium.test_suite.behaviors.tests;

import androidx.test.espresso.action.ViewActions;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.nthChildOf;

import com.healthymedium.arc.library.R;

public class Grid2TestBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        try {
            onView(nthChildOf(withId(R.id.gridLayout), 0)).perform(ViewActions.click());
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "click_1");
            UI.click(R.id.phone);
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "click_phone");
            onView(nthChildOf(withId(R.id.gridLayout), 1)).perform(ViewActions.click());
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "click_2");
            UI.click(R.id.key);
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "click_key");
            onView(nthChildOf(withId(R.id.gridLayout), 2)).perform(ViewActions.click());
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "click_3");
            UI.click(R.id.pen);
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "click_pen");
        } catch (Exception e) {

        }

        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "done");
        UI.click(R.id.buttonContinue);
    }




}
