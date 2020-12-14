package com.healthymedium.test_suite.behaviors.questions;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.healthymedium.test_suite.utilities.ViewActions.setProgress;

public class QuestionRatingBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        // grab the seekbar inside the rating view and set it
       onView(withId(R.id.seekbarRating)).perform(setProgress(25));

        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "dragged");
        UI.sleep(500);
        UI.click(R.id.buttonNext);
    }

}
