package com.healthymedium.test_suite.behaviors.tests;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.GetTextViewMatcher;
import com.healthymedium.test_suite.utilities.UI;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class TestBeginBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "number_" + 3);
        int COUNT = 2;
        while(COUNT > 0){
            onView(withId(R.id.number)).check(matches(new GetTextViewMatcher()));
            int currNumber = Integer.parseInt(GetTextViewMatcher.value);
            if(currNumber == COUNT){
                UI.sleep(100);
                Capture.takeScreenshot(fragment, getClass().getSimpleName(), "number_" + COUNT);
                COUNT--;
            }
        }

        UI.sleep(900);


    }
}
