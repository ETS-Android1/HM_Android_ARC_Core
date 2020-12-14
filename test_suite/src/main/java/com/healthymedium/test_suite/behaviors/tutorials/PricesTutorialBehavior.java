package com.healthymedium.test_suite.behaviors.tutorials;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.library.R;

public class PricesTutorialBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

        UI.sleep(2000);
        UI.click(R.id.radioButtonYes);
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.button_next));
        UI.sleep(1000);
        UI.click(R.id.radioButtonNo);
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.button_next));
        UI.sleep(1000);
        UI.click(R.id.radioButtonNo);
        UI.sleep(1000);
        UI.click(ViewUtil.getString(R.string.button_next));
        UI.sleep(1000);
        UI.click(R.id.radioButtonYes);

        UI.sleep(2000);
        UI.click(R.id.endButton);

    }

}
