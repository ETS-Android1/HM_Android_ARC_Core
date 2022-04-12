package com.healthymedium.arc.paths.setup_v2;
import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.SignInTokenInput;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.ViewUtil;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static com.healthymedium.arc.core.Config.USE_HELP_SCREEN;

@SuppressLint("ValidFragment")
public class Setup2LoginVerificationCodeAsian extends QuestionTemplate {

    String value;
    SignInTokenInput input;
    int maxLength;
    protected LoadingDialog loadingDialog;
    protected TextView textViewError;

    protected TextView textViewProblems;
    protected TextView textViewPolicyLink;
    protected TextView textViewPolicy;

    public static int titleText = R.string.login_enter_raterID;

    public Setup2LoginVerificationCodeAsian() {
        this(true, ViewUtil.getString(titleText),
                null, 9, null);
    }

    public Setup2LoginVerificationCodeAsian(boolean allowBack, String header, String subheader,
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

        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment helpScreen;
                if (USE_HELP_SCREEN) {
                    helpScreen = new HelpScreen();
                } else {
                    helpScreen = new ContactScreen();
                }

                NavigationManager.getInstance().open(helpScreen);
            }
        });

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
        textViewError.setPadding(
                textViewError.getPaddingLeft(),
                ViewUtil.dpToPx(24),
                textViewError.getPaddingRight(),
                textViewError.getPaddingBottom());
        content.addView(textViewError);

        if (value != null) {
            input.setText(value);
        }

        RelativeLayout relativeLayout = (RelativeLayout) view;
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        textViewPolicy = new TextView(getContext());
        textViewPolicy.setText(ViewUtil.getHtmlString(R.string.bysigning_key));
        textViewPolicy.setGravity(Gravity.CENTER_HORIZONTAL);
        textViewPolicy.setTextSize(15);
        linearLayout.addView(textViewPolicy);

        textViewPolicyLink = new TextView(getContext());
        textViewPolicyLink.setTypeface(Fonts.robotoMedium);
        textViewPolicyLink.setPaintFlags(textViewPolicyLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewPolicyLink.setTextColor(ContextCompat.getColor(getContext(),R.color.primary));
        textViewPolicyLink.setGravity(Gravity.CENTER_HORIZONTAL);
        textViewPolicyLink.setText(getResources().getString(R.string.privacy_linked));
        textViewPolicyLink.setTextSize(15);
        textViewPolicyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getPrivacyPolicy().show(getContext());
            }
        });
        linearLayout.addView(textViewPolicyLink);

        RelativeLayout.LayoutParams params0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params0.bottomMargin = ViewUtil.dpToPx(24);
        params0.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params0.addRule(RelativeLayout.ABOVE,buttonNext.getId());
        relativeLayout.addView(linearLayout,params0);

        textViewProblems = new TextView(getContext());
        textViewProblems.setTypeface(Fonts.robotoMedium);

        textViewProblems.setPadding(0, ViewUtil.dpToPx(8), 0, 0);
        textViewProblems.setTextColor(ViewUtil.getColor(R.color.primary));
        textViewProblems.setVisibility(View.INVISIBLE);
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });
        ViewUtil.underlineTextView(textViewProblems);

        // add below textViewError
        int index = content.indexOfChild(textViewError) + 1;
        content.addView(textViewProblems, index);

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
        if (error.string != null && response != null && response.errors != null) {
            if (response.errors.get("errors") != null) {
                error.string += "<br>" + response.errors.get("errors").getAsString();
            } else if (response.errors.get("error") != null) {
                error.string += "<br>" + response.errors.get("error").getAsString();
            }
        }
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
        // Remove the account not found error, as that will be in the base localized error message
        String errorStr = error.replace("<br>Account not found.", "");
        textViewError.setText(Html.fromHtml(errorStr));

        // add this for all errors
        textViewProblems.setText(ViewUtil.getHtmlString(R.string.login_problems_linked));
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });
        textViewProblems.setVisibility(View.VISIBLE);
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
