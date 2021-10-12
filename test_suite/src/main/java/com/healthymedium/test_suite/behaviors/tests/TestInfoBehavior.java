package com.healthymedium.test_suite.behaviors.tests;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.equalTo;

public class TestInfoBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        // if showing hint, click on tutorial
        try {
            onView(withClassName(equalTo(HintPointer.class.getName()))).check(matches(isDisplayed()));
        } catch (Exception e) {
            UI.click(R.id.button);
            return;
        }
        UI.click(R.id.textViewTutorial);

    }
}
