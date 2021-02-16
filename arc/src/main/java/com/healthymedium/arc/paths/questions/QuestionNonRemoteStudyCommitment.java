package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.paths.templates.AltStandardTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionNonRemoteStudyCommitment extends QuestionCheckBoxesAlt {

    public QuestionNonRemoteStudyCommitment(boolean allowBack, String header, String subheader, List<String> options, String exclusive) {
        super(allowBack,header,subheader, options, exclusive);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSubHeaderTextSize(17);
        setSubHeaderLineSpacing(ViewUtil.dpToPx(9), 1);
    }

    @Override
    protected void onNextRequested() {
        Study.getParticipant().markCommittedToStudy();
        Study.getInstance().openNextFragment();
    }
}
