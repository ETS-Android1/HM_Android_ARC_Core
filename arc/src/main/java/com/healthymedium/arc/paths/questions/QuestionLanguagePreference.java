package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class QuestionLanguagePreference extends QuestionRadioButtons {

    public List<Locale> locales;

    public QuestionLanguagePreference(boolean allowBack, boolean allowHelp, String header, String subheader, List<String> options, List<Locale> locales, String button) {
        super(allowBack, allowHelp, header, subheader, options, button);
        this.locales = locales;
    }

    @Override
    public Object onDataCollection(){

        Map<String, Object> response = new HashMap<>();

        Object value = onValueCollection();
        if(value!=null){
            response.put("value", value);
        }

        String language = Locale.LANGUAGE_ENGLISH;
        String country = Locale.COUNTRY_UNITED_STATES;

        selection = options.get((int)value);

        for(Locale locale : locales){
            if(locale.getLabel().equals(selection)){
                language = locale.getLanguage();
                country = locale.getCountry();
                break;
            }
        }

        PreferencesManager.getInstance().putString("language", language);
        PreferencesManager.getInstance().putString("country", country);

        Application.getInstance().updateLocale(getContext());

        return response;
    }

    @Override
    protected void onNextButtonEnabled(boolean enabled) {
        if (enabled) {
            buttonNext.setText("NEXT");
        }
    }

    @Override
    protected void onNextRequested() {
        onDataCollection();
        NavigationManager.getInstance().open(new SplashScreen());
    }
}
