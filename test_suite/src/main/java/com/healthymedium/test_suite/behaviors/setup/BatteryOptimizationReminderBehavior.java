package com.healthymedium.test_suite.behaviors.setup;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;

public class BatteryOptimizationReminderBehavior extends Behavior {
    @Override

    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(1000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");
        UI.click(R.id.button);
        UI.sleep(4000);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "dialog_opened");
        UI.sleep(1000);

    }
}
