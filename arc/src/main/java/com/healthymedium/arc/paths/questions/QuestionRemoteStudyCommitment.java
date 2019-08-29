package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.AltStandardTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionRemoteStudyCommitment extends QuestionPolarAlt {

    public QuestionRemoteStudyCommitment(boolean allowBack, String header, String subheader, String yesAnswer, String noAnswer) {
        super(allowBack,header,subheader, yesAnswer, noAnswer);
    }

    @Override
    protected void onNextRequested() {
        if (answered) {
            if (answerIsYes) {
                // go to next fragment
                Study.getParticipant().markCommittedToStudy();
                Study.getInstance().openNextFragment();
            } else {
                // go to thank you screen
                Study.getParticipant().rebukeCommitmentToStudy();
                Study.getParticipant().save();
                BaseFragment fragment = new AltStandardTemplate(false, ViewUtil.getString(R.string.onboarding_nocommit_header), ViewUtil.getString(R.string.onboarding_nocommit_body), false);
                NavigationManager.getInstance().open(fragment);
            }
        }
    }

}
