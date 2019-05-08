package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.custom.RadioButton;
import com.healthymedium.arc.paths.templates.AltQuestionTemplate;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

// a yes or no question
@SuppressLint("ValidFragment")
public class QuestionSingleButton extends AltQuestionTemplate {

    RadioButton yesButton;
    String buttonText;

    public QuestionSingleButton(boolean allowBack, String header, String subheader, String buttonText) {
        super(allowBack,header,subheader);
        type = "choice";
        this.buttonText = buttonText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        yesButton = new RadioButton(getContext());
        yesButton.setText("Yes");
        yesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText("NEXT");
                }
            }
        });

        content.addView(yesButton);
        buttonNext.setText(buttonText);

        return view;
    }
}
