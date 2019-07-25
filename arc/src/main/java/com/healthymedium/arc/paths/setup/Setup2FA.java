package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.paths.templates.StandardTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2FA extends SetupTemplate {

    public Setup2FA(String header, String subheader, int digitCount) {
        super(true, true, digitCount, 0, header);

        // TODO
        // Send code
    }

    @Override
    protected void onNextRequested() {

        // TODO
        // Figure out how verification works

        //((SetupPathData) Study.getCurrentSegmentData()).authCode = characterSequence.toString();
        super.onNextRequested();
    }

}
