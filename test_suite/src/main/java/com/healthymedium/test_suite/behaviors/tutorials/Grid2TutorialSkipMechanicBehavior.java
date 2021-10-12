package com.healthymedium.test_suite.behaviors.tutorials;

import androidx.test.espresso.action.ViewActions;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;



import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.nthChildOf;

public class Grid2TutorialSkipMechanicBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);


        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "ready_study");
        UI.clickHint();

        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "study");

        UI.sleep(4000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "next");
        UI.clickHint();

        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "hint_tap_F");
        onView(nthChildOf(withId(R.id.gridLayout), 19)).perform(click());

        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "ready_tap_F");
        UI.clickHint();


        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_F");

        UI.sleep(8000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "next");
        UI.clickHint();


        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "ready_test");
        UI.clickHint();

        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "test_opened");


        UI.sleep(10000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_phone_box");
        UI.sleep(1000);
        onView(nthChildOf(withId(R.id.gridLayout), 6)).perform(ViewActions.click());


        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_phone_hint");
        UI.click(R.id.phone);


        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "phone_box_done");




        UI.sleep(2000);
        onView(nthChildOf(withId(R.id.gridLayout), 13)).perform(ViewActions.click());
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_key_box");



        UI.click(R.id.key);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "key_box_done");


        UI.sleep(2000);
        onView(nthChildOf(withId(R.id.gridLayout), 0)).perform(ViewActions.click());
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_other_key_box");



        UI.click(R.id.key);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "other_key_box_done");





        UI.sleep(2000);
        onView(nthChildOf(withId(R.id.gridLayout), 6)).perform(ViewActions.click());
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_phone_box");



        UI.click(R.id.textViewRemoveItem);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "remove_phone_done");






        UI.sleep(2000);
        onView(nthChildOf(withId(R.id.gridLayout), 7)).perform(ViewActions.click());
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_phone_box");



        UI.click(R.id.phone);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "phone_box_done");




        UI.sleep(2000);
        onView(nthChildOf(withId(R.id.gridLayout), 6)).perform(ViewActions.click());
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_pen_box");



        UI.click(R.id.pen);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "pen_box_done");




        UI.sleep(1000);
        UI.click(R.id.buttonContinue);


        UI.sleep(3000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tutorial_complete");
        UI.click(R.id.endButton);


        UI.sleep(5000);
    }

}
