package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.paths.templates.StandardTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.DigitView;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import static com.healthymedium.arc.core.Config.USE_HELP_SCREEN;

@SuppressLint("ValidFragment")
public abstract class Setup2Template extends StandardTemplate {

    int maxDigits;
    int firstDigits;
    int secondDigits;
    int focusedIndex = 0;

    protected CharSequence characterSequence;
    protected EditText editText;
    LinearLayout inputLayout;
    List<DigitView> digits;

    protected LoadingDialog loadingDialog;
    protected TextView textViewProblems;
    protected TextView textViewPolicyLink;
    protected TextView textViewPolicy;
    protected TextView textViewError;

    public Setup2Template(int firstDigitCount, int secondDigitCount, String header) {
        super(true,header,"");
        disableScrollBehavior();
        firstDigits = firstDigitCount;
        secondDigits = secondDigitCount;
        maxDigits = firstDigits + secondDigits;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = super.onCreateView(inflater,container,savedInstanceState);

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

        inputLayout = new LinearLayout(getContext());
        inputLayout.setGravity(Gravity.CENTER);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setPadding(0,50,0,ViewUtil.dpToPx(16));

        content.addView(inputLayout);

        textViewError = new TextView(getContext());
        textViewError.setTextSize(16);
        textViewError.setTextColor(ViewUtil.getColor(R.color.red));
        textViewError.setVisibility(View.INVISIBLE);
        content.addView(textViewError);

        setupEditText();
        content.addView(editText);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.requestFocus();
                showKeyboard(editText);
            }
        };

        content.setOnClickListener(clickListener);
        content.setPadding(ViewUtil.dpToPx(32),50,ViewUtil.dpToPx(32),50);

        digits = new ArrayList<>();
        for(int i=0; i<firstDigits;i++){
            DigitView digitInput = new DigitView(getContext());
            digitInput.setOnClickListener(clickListener);
            digits.add(digitInput);
            inputLayout.addView(digitInput);
        }

        if (secondDigits > 0) {
            addSpacer(16);

            for (int i = 0; i < secondDigits; i++) {
                DigitView digitInput = new DigitView(getContext());
                digitInput.setOnClickListener(clickListener);
                digits.add(digitInput);
                inputLayout.addView(digitInput);
            }
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

        textViewProblems.setPadding(0, ViewUtil.dpToPx(24), 0, 0);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textViewProblems.setPadding(0, ViewUtil.dpToPx(8), 0, 0);
    }

    void addSpacer(int widthDp){
        int width = ViewUtil.dpToPx(widthDp);
        Space space = new Space(getContext());
        space.setLayoutParams(new ViewGroup.LayoutParams(width,width));
        inputLayout.addView(space);
    }

    void setupEditText(){
        editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setCursorVisible(false);
        editText.setBackground(null);
        editText.setTextColor(ContextCompat.getColor(getContext(),android.R.color.transparent));

        // hackish but moves the edittext off the screen while still letting us use it
        editText.animate().translationXBy(ViewUtil.dpToPx(1000));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(isErrorShowing()){
                    hideError();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Log.i("textChanged","start="+start+" before="+before+" count="+count);
                characterSequence = charSequence;
                if(before>count){
                    if(start >= 0){
                        if(start < maxDigits-1){
                            digits.get(start+1).setFocused(false);
                        }

                        digits.get(start).setFocused(true);
                        focusedIndex = start;
                    }
                } else {
                    if(start < maxDigits-1){
                        digits.get(start).setFocused(false);
                        digits.get(start+1).setFocused(true);
                        focusedIndex = start+1;
                    }
                }

                // updateView(charSequence);
                if(charSequence.length()==maxDigits){
                    boolean enabled = isFormValid(charSequence);
                    buttonNext.setEnabled(enabled);
                } else if(buttonNext.isEnabled()){
                    buttonNext.setEnabled(false);
                }
                updateView(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

                if(done && editText.length()==maxDigits){
                    if(!shouldAutoProceed()){
                        hideKeyboard();
                    }else if(buttonNext.isEnabled()){
                        buttonNext.performClick();
                    }
                }
                return false;
            }
        });
        editText.setSingleLine();

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxDigits);
        editText.setFilters(fArray);

    }

    //
    protected boolean isFormValid(CharSequence sequence){
        return true;
    }

    protected abstract boolean shouldAutoProceed();

    protected void updateView(CharSequence s){
        boolean hasError = isErrorShowing();
        int size = s.length();
        for(int i=0;i<size;i++){
            digits.get(i).setDigit(s.charAt(i),hasError);
        }
        int left = digits.size()-size;
        for(int i=size;i<left+size;i++){
            digits.get(i).removeDigit();
        }
    }

    @Override
    protected void onBackRequested() {
        super.onBackRequested();
        hideKeyboard();
    }

    @Override
    public void onPause() {
        super.onPause();
        focusedIndex = editText.getSelectionStart();
        getMainActivity().removeKeyboardListener();
        hideKeyboard();
    }

    @Override
    public void onResume() {
        super.onResume();

        getMainActivity().setKeyboardListener(keyboardToggleListener);

        if(characterSequence!=null) {
            int lastIndex = focusedIndex;
            editText.setText(characterSequence);
            editText.setSelection(lastIndex);

            digits.get(focusedIndex).setFocused(false);
            int digitFocus = characterSequence.length();
            if(digitFocus==maxDigits){
                digitFocus--;
            }
            digits.get(digitFocus).setFocused(true);
            focusedIndex = lastIndex;

            updateView(characterSequence);
        } else {
            digits.get(0).setFocused(true);
        }

    }

    @Override
    protected void onEnterTransitionEnd(boolean popped){
        super.onEnterTransitionEnd(popped);
        editText.requestFocus();
        showKeyboard(editText);
    }

    @Override
    protected void onExitTransitionStart(boolean popped){
        super.onExitTransitionStart(popped);
        hideKeyboard();
    }

    @Override
    protected void onNextRequested() {
        hideKeyboard();
        buttonNext.setEnabled(false);
        if(isErrorShowing()){
            hideError();
        }
    }

    public void hideError(){
        textViewError.setVisibility(View.INVISIBLE);
        textViewError.setText("");
        textViewProblems.setVisibility(View.INVISIBLE);
    }

    public boolean isErrorShowing(){
        return textViewError.getVisibility()==View.VISIBLE;
    }

    public void showError(SetupError error){
        showError(error.string);
//        if(error.link != null) {
//            if (error.link.length() > 0) {
//
//            }
//        }
    }

    public void showError(String error){
        buttonNext.setEnabled(false);
        textViewError.setVisibility(View.VISIBLE);
        textViewError.setText(error);

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
        updateView(editText.getText());

        onErrorShown();
    }

    public void onErrorShown(){

    }

    KeyboardWatcher.OnKeyboardToggleListener keyboardToggleListener = new KeyboardWatcher.OnKeyboardToggleListener() {
        @Override
        public void onKeyboardShown(int keyboardSize) {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.GONE);
                textViewPolicy.setVisibility(View.GONE);
                textViewPolicyLink.setVisibility(View.GONE);
            }
        }

        @Override
        public void onKeyboardClosed() {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.VISIBLE);
                textViewPolicy.setVisibility(View.VISIBLE);
                textViewPolicyLink.setVisibility(View.VISIBLE);
            }
        }
    };

    protected SetupError parseForError(RestResponse response, boolean failed){
        SetupError error = new SetupError();
        error.string = parseForErrorString(response,failed);
        //if(response.code==401 || response.code==406){
            //error.link = getResources().getString(R.string.login_error1_link);
        //}
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


    protected RestClient.Listener registrationListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            SetupError error = parseForError(response,false);
            loadingDialog.dismiss();
            if(error.string==null) {
                String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
                Study.getParticipant().getState().id = id;
                if(Config.REPORT_STUDY_INFO){
                }
                Study.openNextFragment();
            } else {
                showError(error);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            SetupError error = parseForError(response,true);
            showError(error);
            loadingDialog.dismiss();
        }
    };

    class SetupError {
        String string;
        String link;
    }

}
