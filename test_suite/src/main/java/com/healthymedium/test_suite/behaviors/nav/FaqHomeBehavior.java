package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.nthChildOf;

public class FaqHomeBehavior extends Behavior {

    int index = 0;
    int size = 3;


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        if(index == 0){
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        }
        if(index < size) {
            onView(nthChildOf(withId(com.healthymedium.arc.library.R.id.bottomLinearLayout), index)).perform(click());
            index++;
        }
    }
}
