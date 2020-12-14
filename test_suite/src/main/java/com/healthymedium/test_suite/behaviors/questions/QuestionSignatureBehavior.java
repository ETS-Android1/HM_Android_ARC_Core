package com.healthymedium.test_suite.behaviors.questions;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.Signature;
import com.healthymedium.arc.library.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.endsWith;


public class QuestionSignatureBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        onView(withClassName(endsWith(Signature.class.getName()))).perform(click());
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "clicked");
        UI.sleep(500);
        onView(withId(R.id.buttonNext)).perform(click());
    }

}
