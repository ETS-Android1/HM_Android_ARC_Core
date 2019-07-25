package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class SetupAuthCode extends SetupTemplate {

    public SetupAuthCode(boolean authenticate, boolean is2FA, int digitCount, String header) {
        super(authenticate, is2FA, digitCount,0, header);

    }

    @Override
    protected void onNextRequested() {
        ((SetupPathData) Study.getCurrentSegmentData()).authCode = characterSequence.toString();
        super.onNextRequested();
    }

}
