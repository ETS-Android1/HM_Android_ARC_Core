package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.DigitView;
import com.healthymedium.arc.utilities.ViewUtil;

import androidx.annotation.Nullable;

@SuppressLint("ValidFragment")
public class Setup2LoginVerificationCode extends Setup2Template {

    public static final int DIGIT_COUNT = 9;

    public Setup2LoginVerificationCode() {
        super(DIGIT_COUNT, 0, ViewUtil.getString(R.string.login_enter_2FA));
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // We need uppercase, lowercase, number, and special characters
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        // Make input layout full width, with a weighted sum
        ViewGroup.LayoutParams inputLayoutParams = inputLayout.getLayoutParams();
        inputLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        inputLayout.setWeightSum(DIGIT_COUNT);
        inputLayout.setLayoutParams(inputLayoutParams);
        inputLayout.setPadding(ViewUtil.dpToPx(16), inputLayout.getPaddingTop(),
                ViewUtil.dpToPx(16), inputLayout.getPaddingBottom());

        // Make the digit views smaller, as we need to fit [DIGIT_COUNT] in
        for (int i = 0; i < inputLayout.getChildCount(); i++) {
            View child = inputLayout.getChildAt(i);
            if (child instanceof DigitView) {
                DigitView digitView = (DigitView)child;
                LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams)digitView.getLayoutParams();
                params.weight = 1;
                params.width = 0;  // width will be set by weight
                digitView.setLayoutParams(params);
            }
        }

        return view;
    }

    @Override
    public void addSpacer(int widthDp) {
        // Do nothing, spacers will be added through weight sum gravity
    }

    @Override
    protected boolean shouldAutoProceed() {
        return true;
    }

    private boolean fragmentExists(PathSegment path, Class tClass) {
        int last = path.fragments.size()-1;
        String oldName = path.fragments.get(last).getSimpleTag();
        String newName = tClass.getSimpleName();
        return oldName.equals(newName);
    }

    @Override
    protected void onNextRequested() {
        SetupPathData pathData = (SetupPathData)Study.getCurrentSegmentData();
        pathData.authCode = characterSequence.toString();

        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        Study.getRestClient().registerDevice(
                pathData.id, pathData.authCode, false, registrationListener);
    }

    protected RestClient.Listener registrationListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            Setup2Template.SetupError error = parseForError(response,false);
            loadingDialog.dismiss();
            if(error.string==null) {
                Study.getParticipant().getState().id =
                        ((SetupPathData)Study.getCurrentSegmentData()).id;;
                Study.openNextFragment();
            } else {
                showError(error.string);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            Setup2Template.SetupError error = parseForError(response,true);
            showError(error.string);
            loadingDialog.dismiss();
        }
    };

    protected Setup2Template.SetupError parseForError(RestResponse response, boolean failed){
        Setup2Template.SetupError error = new Setup2Template.SetupError();
        error.string = parseForErrorString(response,failed);
        return error;
    }

    protected String parseForErrorString(RestResponse response, boolean failed){
        int code = response.code;
        switch (code){
            case 400:
                return getResources().getString(R.string.login_error3);
            case 401:
                return getResources().getString(R.string.login_error1);
            case 406:
                return getResources().getString(R.string.login_error1);
            case 409:
                return getResources().getString(R.string.login_error2);
        }
        if(response.errors.keySet().size()>0){
            String key = response.errors.keySet().toArray()[0].toString();
            return response.errors.get(key).getAsString();
        }
        if(!response.successful || failed){
            return getResources().getString(R.string.login_error3);
        }
        return null;
    }

    public void showError(String error) {
        textViewError.setVisibility(View.VISIBLE);
        textViewError.setText(error);
    }

    public void hideError(){
        textViewError.setVisibility(View.INVISIBLE);
        textViewError.setText("");
    }

    public boolean isErrorShowing(){
        return textViewError.getVisibility()==View.VISIBLE;
    }

}
