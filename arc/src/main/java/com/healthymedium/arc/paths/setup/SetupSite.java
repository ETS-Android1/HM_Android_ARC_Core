package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class SetupSite extends SetupTemplate {

    LoadingDialog loadingDialog;
    TextView textViewProblems;

    public SetupSite(int digitCount) {
        super(digitCount,0, ViewUtil.getString(R.string.login_enter_raterID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        textViewProblems = new TextView(getContext());
        textViewProblems.setTypeface(Fonts.robotoMedium);
        textViewProblems.setPadding(0,ViewUtil.dpToPx(24),0,0);
        textViewProblems.setText("Problems logging in?");
        textViewProblems.setTextColor(ViewUtil.getColor(R.color.primary));
        textViewProblems.setVisibility(View.INVISIBLE);
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpScreen contactScreen = new HelpScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });
        ViewUtil.underlineTextView(textViewProblems);

        // add below textViewError
        int index = content.indexOfChild(textViewError)+1;
        content.addView(textViewProblems,index);
        return view;
    }

    @Override
    protected void onNextRequested(){
        buttonNext.setEnabled(false);
        hideKeyboard();

        if(Config.REST_BLACKHOLE){
            String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
            Study.getParticipant().getState().id = id;
            Study.getInstance().openNextFragment();
            return;
        }

        if(isErrorShowing()){
            hideError();
        }

        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        String siteCode = editText.getText().toString();
        String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
        Study.getRestClient().registerDevice(id, siteCode, false, registrationListener);

    }

    @Override
    public void hideError(){
        super.hideError();
        textViewProblems.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error){
        super.showError(error);
        textViewProblems.setVisibility(View.VISIBLE);
        updateView(editText.getText());
    }

    String parseForError(RestResponse response, boolean failed){
        int code = response.code;
        switch (code){
            case 400:
                return getResources().getString(R.string.error3);
            case 401:
                return getResources().getString(R.string.error1);
            case 409:
                return getResources().getString(R.string.error2);
        }
        if(response.errors.keySet().size()>0){
            String key = response.errors.keySet().toArray()[0].toString();
            return response.errors.get(key).getAsString();
        }
        if(!response.successful || failed){
            return getResources().getString(R.string.error3);
        }
        return null;
    }

    RestClient.Listener registrationListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            String errorString = parseForError(response,false);
            loadingDialog.dismiss();
            if(errorString==null) {
                String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
                Study.getParticipant().getState().id = id;
                Study.openNextFragment();
            } else {
                showError(errorString);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            String errorString = parseForError(response,true);
            showError(errorString);
            loadingDialog.dismiss();
        }
    };

}
