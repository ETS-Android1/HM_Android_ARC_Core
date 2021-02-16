package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionRadioButtons extends QuestionTemplate {

    List<RadioButton> buttons;
    List<String> options;
    String selection;
    int lastIndex = -1;
    boolean allowHelp;

    public QuestionRadioButtons(boolean allowBack, boolean allowHelp, String header, String subheader, List<String> options) {
        super(allowBack,header,subheader);
        this.options = options;
        this.allowHelp = allowHelp;
        type = "choice";
    }

    public QuestionRadioButtons(boolean allowBack, boolean allowHelp, String header, String subheader, List<String> options, String button) {
        super(allowBack,header,subheader, button);
        this.options = options;
        this.allowHelp = allowHelp;
        type = "choice";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(allowHelp);

        buttons = new ArrayList<>();
        int index=0;
        for(String option : options){
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option);

            final int radioButtonIndex = index;
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    response_time = System.currentTimeMillis();
                    if(b) {
                        if (radioButtonIndex != lastIndex) {
                            if (lastIndex != -1) {
                                buttons.get(lastIndex).setChecked(false);
                            }
                            //buttons.get(radioButtonIndex).setChecked(true);
                            selection = options.get(radioButtonIndex);
                            lastIndex = radioButtonIndex;


                        }
                        if (!buttonNext.isEnabled()) {
                            buttonNext.setEnabled(true);
                            onNextButtonEnabled(true);
                        }
                    }
                }
            });
            buttons.add(radioButton);
            content.addView(radioButton);
            index++;
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(buttons.size()>0 && lastIndex>-1) {
            buttons.get(lastIndex).setChecked(true);
        }
    }

    @Override
    public Object onValueCollection(){
        return lastIndex;
    }

    @Override
    public String onTextValueCollection(){
        return selection;
    }

}
