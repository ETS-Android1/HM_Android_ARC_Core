package com.healthymedium.arc.paths.setup_v2;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.SignInTokenInput;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.ViewUtil;

import androidx.annotation.Nullable;

@SuppressLint("ValidFragment")
public class Setup2AuthSignInToken extends QuestionTemplate {

    String value;
    SignInTokenInput input;
    int maxLength;
    protected LoadingDialog loadingDialog;
    protected TextView textViewError;

    public Setup2AuthSignInToken() {
        this(true, ViewUtil.getString(R.string.login_enter_raterID),
                null, 20, null);
    }

    public Setup2AuthSignInToken(boolean allowBack, String header, String subheader,
                       int maxLength, String initialValue) {
        super(allowBack,header,subheader);
        this.maxLength = maxLength;
        type = "multilineText";
        this.value = initialValue;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        input = new SignInTokenInput(getContext());
        input.setMaxLength(maxLength);
        input.setListener(new SignInTokenInput.Listener() {
            @Override
            public void onValueChanged() {
                if(isErrorShowing()){
                    hideError();
                }
                response_time = System.currentTimeMillis();
                if(input.getString().isEmpty()){
                    buttonNext.setEnabled(false);
                } else if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                }
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean done = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    done = true;
                }

                if(keyEvent!=null){
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        done = true;
                    }
                }

                if(done && input.length()>0){
//                    buttonNext.performClick();
                }
                return false;
            }
        });
        input.setSingleLine();
        content.addView(input);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = ViewUtil.dpToPx(32);
        params.rightMargin = ViewUtil.dpToPx(32);
        params.topMargin = ViewUtil.dpToPx(19);
        content.setLayoutParams(params);

        textViewError = new TextView(getContext());
        textViewError.setTextSize(16);
        textViewError.setTextColor(ViewUtil.getColor(R.color.red));
        textViewError.setVisibility(View.INVISIBLE);
        content.addView(textViewError);

        if (value != null) {
            input.setText(value);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(input !=null) {
            value = input.getString();
        }
        hideKeyboard();
        getMainActivity().removeKeyboardListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainActivity().setKeyboardListener(keyboardToggleListener);

    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        if(input != null) {
            input.setText(value);
            input.requestFocus();
            showKeyboard(input);
        }
    }

    @Override
    public Object onValueCollection(){
        if(input != null) {
            value = input.getString();
        }
        return value;
    }

    @Override
    protected void onNextRequested() {
        SetupPathData pathData = (SetupPathData)Study.getCurrentSegmentData();
        pathData.authCode = input.getString();

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

    KeyboardWatcher.OnKeyboardToggleListener keyboardToggleListener = new KeyboardWatcher.OnKeyboardToggleListener() {
        @Override
        public void onKeyboardShown(int keyboardSize) {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.GONE);
            }
        }

        @Override
        public void onKeyboardClosed() {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.VISIBLE);
            }
        }
    };
}
