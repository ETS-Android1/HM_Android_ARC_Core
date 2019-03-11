package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.custom.Signature;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

@SuppressLint("ValidFragment")
public class QuestionSignature extends QuestionTemplate {

    boolean allowHelp;

    public QuestionSignature(boolean allowBack, boolean allowHelp, String header, String subheader) {
        super(allowBack,header,subheader,"SUBMIT");
        this.allowHelp = allowHelp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(allowHelp);

        Signature signature = new Signature(getContext());
        content.addView(signature);

        return view;
    }
}
