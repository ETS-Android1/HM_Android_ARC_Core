package com.healthymedium.arc.study;

import android.content.Context;

import com.healthymedium.arc.paths.informative.PrivacyScreen;
import com.healthymedium.arc.utilities.NavigationManager;

public class PrivacyPolicy {

    public PrivacyPolicy() {

    }

    public void show(Context context) {
        PrivacyScreen privacyScreen = new PrivacyScreen();
        NavigationManager.getInstance().open(privacyScreen);
    }
}