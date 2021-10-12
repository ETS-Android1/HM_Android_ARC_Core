package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2AuthManual extends Setup2Template {


    public Setup2AuthManual(int digitCount) {
        super(digitCount,0, ViewUtil.getString(R.string.login_enter_2FA));
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();


        ((SetupPathData) Study.getCurrentSegmentData()).authCode = characterSequence.toString();


        if(Config.REST_BLACKHOLE){
            String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
            Study.getParticipant().getState().id = id;
            Study.getInstance().openNextFragment();
            return;
        }

        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        SetupPathData pathData = (SetupPathData)Study.getCurrentSegmentData();
        Study.getRestClient().registerDevice(pathData.id, pathData.authCode, false, registrationListener);

    }

    @Override
    public void onErrorShown(){

    }

    public void hideError(){
        super.hideError();
    }

    @Override
    protected boolean shouldAutoProceed() {
        return false;
    }
}
