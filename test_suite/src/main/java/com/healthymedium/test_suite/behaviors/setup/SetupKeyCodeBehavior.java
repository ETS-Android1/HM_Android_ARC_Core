package com.healthymedium.test_suite.behaviors.setup;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

public class SetupKeyCodeBehavior extends Behavior {

    String txt = null;
    public SetupKeyCodeBehavior(String txt){
        this.txt = txt;
    }

    @Override
    public void onOpened(BaseFragment fragment) {
        super.onOpened(fragment);

        String id;
        switch (txt){
            case "id":
                id = TestBehavior.login.id;
                break;
            case "confirm":
                id = TestBehavior.login.idConfirm;
                break;
            case "auth":
                id = TestBehavior.login.authCode;
                break;
            default:
                id = "000000";

        }
        UI.sleep(5000);

        ViewInteraction root = Espresso.onView(ViewMatchers.isRoot());
        int length = id.length();
        for(int i = 0; i<length; i++){
            char character = id.charAt(i);
            root.perform(ViewActions.pressKey(getKeyCode(character)));
            UI.sleep(100);
        }
        root.perform(ViewActions.closeSoftKeyboard());
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "after");
        UI.click(R.id.buttonNext);
    }

    // unicode '0' = 48
    // keyEvent.Key_0 = 7
    int getKeyCode(char character){
        return Integer.valueOf(character)-41;
    }

}
