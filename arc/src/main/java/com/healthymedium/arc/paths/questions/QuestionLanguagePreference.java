package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        String language = "en";
        String country = "US";

        selection = options.get((int)value);

        if (selection.equals("English")) {
            language = "en";
            country = "US";
        }
        else if (selection.equals("Français")) {
            language = "fr";
            country = "FR";
        }
        else if (selection.equals("Español")) {
            language = "es";
            country = "ES";
        }

        PreferencesManager.getInstance().putString("language", language);
        PreferencesManager.getInstance().putString("country", country);

        Resources res = getContext().getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language,country);
        res.updateConfiguration(conf, res.getDisplayMetrics());

        return response;
    }


    @Override
    protected void onNextRequested() {
        onDataCollection();
        Study.getInstance().openNextFragment();
    }
}
