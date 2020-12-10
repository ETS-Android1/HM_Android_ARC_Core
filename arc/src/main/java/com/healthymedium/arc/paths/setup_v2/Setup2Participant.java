package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2Participant extends Setup2Template {

    public Setup2Participant(int firstDigitCount, int secondDigitCount) {
        super(firstDigitCount, secondDigitCount, ViewUtil.getString(R.string.login_enter_ARCID));
    }

    @Override
    protected boolean shouldAutoProceed() {
        return true;
    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();

        SetupPathData setupPathData = (SetupPathData) Study.getCurrentSegmentData();
        setupPathData.id = characterSequence.toString();
        Study.getInstance().openNextFragment();
    }

}
