package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class SetupParticipantConfirm extends SetupTemplate {

    CharSequence previousCharacterSequence = "";

    public SetupParticipantConfirm(boolean authenticate, boolean is2FA, int firstDigitCount, int secondDigitCount) {
        super(authenticate, is2FA, firstDigitCount, secondDigitCount, ViewUtil.getString(R.string.login_confirm_ARCID));
    }

    @Override
    protected boolean isFormValid(CharSequence sequence) {
        if (sequence.toString().equals(previousCharacterSequence.toString())) {
            return true;
        } else {
            showError(Application.getInstance().getResources().getString(R.string.login_error1));
            return false;
        }
    }

    @Override
    public void onResume() {
        String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
        previousCharacterSequence = id;
        super.onResume();
    }

}
