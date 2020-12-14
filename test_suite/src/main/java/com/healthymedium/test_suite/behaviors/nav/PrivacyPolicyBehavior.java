package com.healthymedium.test_suite.behaviors.nav;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.library.R;

public class PrivacyPolicyBehavior  extends Behavior {
    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        UI.sleep(500);
    }
}