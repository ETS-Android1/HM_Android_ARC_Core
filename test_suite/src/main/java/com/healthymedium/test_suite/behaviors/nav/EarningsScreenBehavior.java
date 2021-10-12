package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.Matchers.nthChildOf;



public class EarningsScreenBehavior extends Behavior {



    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");


        try {
            onView(nthChildOf(withId(R.id.goalLayout), 0)).perform(scrollTo());
            UI.sleep(2000);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "44_goal");

            onView(nthChildOf(withId(R.id.goalLayout), 1)).perform(scrollTo());
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "2day_goal");

            onView(nthChildOf(withId(R.id.goalLayout), 2)).perform(scrollTo());
            UI.sleep(500);
            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "21_goal");
        } catch (Exception e) {
            UI.sleep(500);
        }





        onView(withId(R.id.viewFaqButton)).perform(scrollTo());
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "faq");

        //go to details
        onView(withId(R.id.viewDetailsButton)).perform(scrollTo());
        UI.sleep(500);
        UI.click(ViewUtil.getString(R.string.button_viewdetails));


    }
}