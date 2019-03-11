package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;

import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class QuestionLanguagePreference extends QuestionRadioButtons {

    public QuestionLanguagePreference(boolean allowBack, boolean allowHelp, String header, String subheader, List<String> options) {
        super(allowBack, allowHelp, header, subheader, options);
    }

    @Override
    public Object onDataCollection(){

        Map<String, Object> response = new HashMap<>();

        Object value = onValueCollection();
        if(value!=null){
            response.put("value", value);
        }

        selection = options.get((int)value);

        if (selection.equals("English")) {
            PreferencesManager.getInstance().putString("language", "en");
        }
        else if (selection.equals("Français")) {
            PreferencesManager.getInstance().putString("language", "fr");
        }
        else if (selection.equals("Español")) {
            PreferencesManager.getInstance().putString("language", "es");
        }

        return response;
    }


    @Override
    protected void onNextRequested() {
        onDataCollection();
        Study.getInstance().openNextFragment();
    }
}
