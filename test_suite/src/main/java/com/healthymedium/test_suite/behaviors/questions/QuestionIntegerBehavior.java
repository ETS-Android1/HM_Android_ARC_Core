package com.healthymedium.test_suite.behaviors.questions;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import android.view.KeyEvent;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;


public class QuestionIntegerBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(1000);
        ViewInteraction root = Espresso.onView(ViewMatchers.isRoot());
        root.perform(ViewActions.pressKey(KeyEvent.KEYCODE_1));
        root.perform(ViewActions.closeSoftKeyboard());
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "pressed");
        UI.sleep(500);
        UI.click(R.id.buttonNext);
    }

}
