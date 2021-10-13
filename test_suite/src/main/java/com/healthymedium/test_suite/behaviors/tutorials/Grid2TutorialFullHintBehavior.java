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

public class Grid2TutorialFullHintBehavior extends Behavior {

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



        UI.sleep(5000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "popup_remindme");
        UI.clickHint();



        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "remind_2_boxes");
        onView(nthChildOf(withId(R.id.gridLayout), 13)).perform(ViewActions.click());



        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_key");
        UI.click(R.id.key);


        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "key_box_done");



        UI.sleep(5000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "popup_remindme");
        UI.clickHint();




        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "remind_1_box");
        onView(nthChildOf(withId(R.id.gridLayout), 15)).perform(ViewActions.click());



        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_pen");
        UI.click(R.id.pen);


        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "pen_box_done");
        UI.click(R.id.buttonContinue);


        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "show_me");
        UI.clickHint();



        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "okay");
        UI.clickHint();


        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_different_box");
        onView(nthChildOf(withId(R.id.gridLayout), 8)).perform(ViewActions.click());


        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_different_box_phone");
        UI.click(R.id.phone);



        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tap_remove_hint");
        onView(nthChildOf(withId(R.id.gridLayout), 8)).perform(ViewActions.click());


        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "remove_item_hint");
        UI.click(R.id.textViewRemoveItem);


        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "finish_tutorial");
        UI.clickHint();

        UI.sleep(3000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "tutorial_complete");
        UI.click(R.id.endButton);


        UI.sleep(5000);
    }

}
