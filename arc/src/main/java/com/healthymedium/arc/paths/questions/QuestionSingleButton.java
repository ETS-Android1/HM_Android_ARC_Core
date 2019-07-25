package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.ui.CheckBox;
import com.healthymedium.arc.paths.templates.AltQuestionTemplate;

// a yes or no question
@SuppressLint("ValidFragment")
public class QuestionSingleButton extends AltQuestionTemplate {

    CheckBox box;
    String buttonText;
    String optionText;

    public QuestionSingleButton(boolean allowBack, String header, String subheader, String buttonText, String optionText) {
        super(allowBack,header,subheader);
        type = "choice";
        this.buttonText = buttonText;
        this.optionText = optionText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        box = new CheckBox(getContext());
        box.setText(optionText);

        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText("NEXT");
                } else {
                    buttonNext.setEnabled(false);
                    onNextButtonEnabled(false);
                }
            }
        });

        content.addView(box);
        buttonNext.setText(buttonText);

        return view;
    }
}
