package com.healthymedium.test_suite.behaviors.questions;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.CheckBox;
import com.healthymedium.arc.library.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.first;
import static org.hamcrest.Matchers.endsWith;


public class QuestionCheckBoxesBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        onView(first(withClassName(endsWith(CheckBox.class.getName())))).perform(click());
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "clicked");
        onView(withId(R.id.scrollView)).perform(swipeUp());
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "scrolled");
        UI.sleep(500);
        UI.click(R.id.buttonNext);
    }

}
