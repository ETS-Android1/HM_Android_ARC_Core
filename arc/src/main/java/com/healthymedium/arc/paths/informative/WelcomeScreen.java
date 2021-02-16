package com.healthymedium.arc.paths.informative;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.paths.templates.InfoTemplate;

@SuppressLint("ValidFragment")
public class WelcomeScreen extends InfoTemplate {

    RadioButton understandButton;

    public WelcomeScreen(boolean allowBack, String header, String body, @Nullable String buttonText) {
        super(allowBack,header,body, buttonText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        button.setEnabled(false);

        understandButton = new RadioButton(getContext());
        understandButton.setText("I understand");
        understandButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!button.isEnabled()){
                    button.setEnabled(true);
                    button.setText("NEXT");
                }
            }
        });

        content.addView(understandButton);

        return view;
    }
}
