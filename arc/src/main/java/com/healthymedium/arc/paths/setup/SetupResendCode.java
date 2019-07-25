package com.healthymedium.arc.paths.setup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.FAQAnswerScreen;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.paths.templates.StandardTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class SetupResendCode extends BaseFragment {

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubHeader;
    TextView textViewError;
    TextView textViewProblems;

    Button newCodeButton;

    LinearLayout content;

    public SetupResendCode() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resend_code, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackRequested();
            }
        });

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(ViewUtil.getString(R.string.login_resend_header));

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setText(ViewUtil.getString(R.string.login_resend_subheader));

        newCodeButton = view.findViewById(R.id.newCodeButton);
        newCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Study.getRestClient().requestVerificationCode(verificationCodeListener);
            }
        });

        textViewError = new TextView(getContext());
        textViewError.setTextSize(16);
        textViewError.setTextColor(ViewUtil.getColor(R.color.red));
        textViewError.setVisibility(View.INVISIBLE);
        content.addView(textViewError);

        textViewProblems = new TextView(getContext());
        textViewProblems.setTypeface(Fonts.robotoMedium);
        textViewProblems.setPadding(0, ViewUtil.dpToPx(24), 0, 0);
        textViewProblems.setText("I need more help");
        textViewProblems.setTextColor(ViewUtil.getColor(R.color.primary));
        textViewProblems.setVisibility(View.VISIBLE);
        textViewProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQAnswerScreen helpScreen = new FAQAnswerScreen("How do I resolve two-step verification issues?", "I don't know.");
                NavigationManager.getInstance().open(helpScreen);
            }
        });
        ViewUtil.underlineTextView(textViewProblems);

        // add below textViewError
        int index = content.indexOfChild(textViewError) + 1;
        content.addView(textViewProblems, index);

        return view;
    }

    protected void onBackRequested() {
        Log.i("SetupResendCode","onBackRequested");
        NavigationManager.getInstance().popBackStack();
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

    RestClient.Listener verificationCodeListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            String errorString = parseForError(response,false);
            if(errorString!=null) {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText(errorString);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            String errorString = parseForError(response,true);
            textViewError.setVisibility(View.VISIBLE);
            textViewError.setText(errorString);
        }
    };
}
