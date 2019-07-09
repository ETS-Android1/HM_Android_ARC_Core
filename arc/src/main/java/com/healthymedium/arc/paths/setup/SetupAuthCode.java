package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class SetupAuthCode extends SetupTemplate {

    public SetupAuthCode(boolean authenticate, int digitCount) {
        super(authenticate,digitCount,0, ViewUtil.getString(R.string.login_enter_raterID));
    }

    @Override
    protected void onNextRequested() {
        ((SetupPathData) Study.getCurrentSegmentData()).authCode = characterSequence.toString();
        super.onNextRequested();
    }

}
