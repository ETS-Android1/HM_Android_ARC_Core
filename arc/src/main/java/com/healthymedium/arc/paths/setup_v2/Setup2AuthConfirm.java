package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2AuthConfirm extends Setup2Template {


    public Setup2AuthConfirm(int digitCount) {
        super(digitCount,0, ViewUtil.getString(R.string.login_enter_2FA));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewProblems.setPadding(0, ViewUtil.dpToPx(8), 0, 0);
        textViewProblems.setText(ViewUtil.getString(R.string.login_problems_2FA));
        textViewProblems.setVisibility(View.VISIBLE);
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Setup2AuthConfirmResend setupResendCodeScreen = new Setup2AuthConfirmResend();
                NavigationManager.getInstance().open(setupResendCodeScreen);
            }
        });
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
        textViewProblems.setText(ViewUtil.getHtmlString(R.string.login_problems_linked));
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });
        textViewProblems.setVisibility(View.VISIBLE);
        updateView(editText.getText());
    }

    public void hideError(){
        super.hideError();
        textViewProblems.setVisibility(View.INVISIBLE);
    }

    @Override
    protected boolean shouldAutoProceed() {
        return false;
    }
}
