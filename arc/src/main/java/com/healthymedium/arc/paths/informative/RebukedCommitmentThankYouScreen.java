package com.healthymedium.arc.paths.informative;

import android.annotation.SuppressLint;

import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.paths.templates.AltStandardTemplate;

@SuppressLint("ValidFragment")
public class RebukedCommitmentThankYouScreen extends AltStandardTemplate {

    public RebukedCommitmentThankYouScreen(boolean allowBack, String header, String subheader, Boolean showButton) {
        super(allowBack, header, subheader, showButton);
    }

    @Override
    protected void onBackRequested() {
        NavigationManager.getInstance().popBackStack();
    }

}
