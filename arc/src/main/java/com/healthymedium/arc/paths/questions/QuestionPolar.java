package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.custom.RadioButton;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.utilities.ViewUtil;

// a yes or no question
@SuppressLint("ValidFragment")
public class QuestionPolar extends QuestionTemplate {

    RadioButton yesButton;
    RadioButton noButton;
    protected boolean answerIsYes;
    protected boolean answered;

    public QuestionPolar(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader);
        type = "choice";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        yesButton = new RadioButton(getContext());
        yesButton.setText(ViewUtil.getString(R.string.YES));
        yesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                answered = true;
                if(b){
                    response_time = System.currentTimeMillis();
                    answerIsYes = true;
                    noButton.setChecked(false);
                }

                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText(ViewUtil.getString(R.string.button_next));
                }
            }
        });

        noButton = new RadioButton(getContext());
        noButton.setText(ViewUtil.getString(R.string.NO));
        noButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                answered = true;
                if(b){
                    response_time = System.currentTimeMillis();
                    answerIsYes = false;
                    yesButton.setChecked(false);
                }

                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText(ViewUtil.getString(R.string.button_next));
                }
            }
        });

        content.addView(yesButton);
        content.addView(noButton);
        buttonNext.setText(ViewUtil.getString(R.string.button_confirm));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(answered){
            if(answerIsYes){
                yesButton.setChecked(true);
            } else {
                noButton.setChecked(true);
            }
        }
    }

    @Override
    public Object onValueCollection(){
        if(!answered){
            return -1;
        }
        return answerIsYes ? 1:0;
    }

    @Override
    public String onTextValueCollection(){
        if(answered){
            return answerIsYes ? "Yes":"No";
        }
        return null;
    }
}
