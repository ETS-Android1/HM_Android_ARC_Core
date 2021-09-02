package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.content.res.Resources;
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
import com.healthymedium.arc.ui.PhoneInput;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.ViewUtil;

import androidx.annotation.Nullable;

@SuppressLint("ValidFragment")
public class Setup2Phone extends QuestionTemplate {

    String value;
    PhoneInput input;
    int maxLength;
    protected LoadingDialog loadingDialog;
    protected TextView textViewError;


    public Setup2Phone(boolean allowBack, String header, String subheader,
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

        input = new PhoneInput(getContext());
        input.setMaxLength(maxLength);
        input.setListener(new PhoneInput.Listener() {
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
        if(isErrorShowing()){
            hideError();
        }

        value = input.getString();

        if(Config.REST_BLACKHOLE){
            String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
            Study.getParticipant().getState().id = id;
            Study.getInstance().openNextFragment();
            return;
        }

        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        SetupPathData pathData = (SetupPathData)Study.getCurrentSegmentData();
        Study.getRestClient().requestVerificationCode(value, requestVerificationListener);
    }

    RestClient.Listener requestVerificationListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {

            loadingDialog.dismiss();

            if(response.code >= 400 || !response.successful) {
                showError(getResources().getString(R.string.login_error1));
                loadingDialog.dismiss();
                return;
            }

            Study.openNextFragment();
        }

        @Override
        public void onFailure(RestResponse response) {
            showError(getResources().getString(R.string.login_error1));
            loadingDialog.dismiss();
        }
    };

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

    private boolean fragmentExists(PathSegment path, Class tClass) {
        int last = path.fragments.size()-1;
        String oldName = path.fragments.get(last).getSimpleTag();
        String newName = tClass.getSimpleName();
        return oldName.equals(newName);
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
