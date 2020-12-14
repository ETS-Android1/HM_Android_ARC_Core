package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;



public class ProgressScreenBehavior extends Behavior {


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "session_week");
        onView(withId(R.id.viewFaqButton)).perform(scrollTo());
        UI.sleep(2000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "study");
        UI.sleep(250);

        //The automated test will crash if testDay = 0 and testCycle = 0
        if ( Study.getParticipant().getState().currentTestDay == 0 && Study.getParticipant().getState().currentTestCycle == 0){
            Study.getParticipant().getState().currentTestDay = 1;
            Study.getParticipant().getState().currentTestCycle = 1;
        }



        UI.click(ViewUtil.getString(R.string.resources_nav_home));
    }
}