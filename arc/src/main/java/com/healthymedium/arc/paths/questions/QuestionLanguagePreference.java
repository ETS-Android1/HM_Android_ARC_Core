package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class QuestionLanguagePreference extends QuestionRadioButtons {

    public List<java.util.Locale> locales;

    public QuestionLanguagePreference(boolean allowBack, boolean allowHelp, String header, String subheader, List<String> options, List<java.util.Locale> locales, String button) {
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


        for(java.util.Locale locale : locales){
            if(Locale.getLabel(locale).equals(selection)){
                Locale.update(locale, getApplication().getApplicationContext());
            }
        }

        return response;
    }

    @Override
    protected void onNextButtonEnabled(boolean enabled) {
        if (enabled) {
            buttonNext.setText(ViewUtil.getString(R.string.button_next));
        }
    }

    @Override
    protected void onNextRequested() {
        onDataCollection();
        NavigationManager.getInstance().open(new SplashScreen());
    }
}
