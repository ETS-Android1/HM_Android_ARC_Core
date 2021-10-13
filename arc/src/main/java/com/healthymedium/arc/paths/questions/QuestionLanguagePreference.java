package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.font.FontFactory;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.BuildConfig;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class QuestionLanguagePreference extends QuestionRadioButtons {

    static List<Locale> locales;

    // Switch to false during QA to see all locales
    static Boolean hideUnsupportedLocales = true;

    public QuestionLanguagePreference() {
        super(false, true, "Language:", "", initOptions(), "CONFIRM");

        if(FontFactory.getInstance()==null) {
            FontFactory.initialize(Application.getInstance().getAppContext());
        }

        if(!Fonts.areLoaded()){
            Fonts.load();
            FontFactory.getInstance().setDefaultFont(Fonts.roboto);
            FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        // assumes same size and order between locales and options
        int size = buttons.size();
        for(int i=0;i<size;i++) {
            if(!locales.get(i).IsfullySupported()) {
                buttons.get(i).setAlpha(0.4f);
            }
        }

        return view;
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

        PreferencesManager.getInstance().putStringImmediately(Locale.TAG_LANGUAGE, language);
        PreferencesManager.getInstance().putStringImmediately(Locale.TAG_COUNTRY, country);

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
        triggerAppRestart();
    }

    /**
     * An app restart is necessary for forced localization to take effect.
     * See this blog post:
     * https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
     */
    public void triggerAppRestart() {
        Context context = getContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    static List<String> initOptions() {

        List<Locale> AllLocales = Application.getInstance().getLocaleOptions();
        List<String> options = new ArrayList<>();
        locales = new ArrayList<>();

        for(Locale locale : AllLocales) {
            if(hideUnsupportedLocales) {
                if(locale.IsfullySupported()){
                    options.add(locale.getLabel());
                    locales.add(locale);
                }
            } else {
                options.add(locale.getLabel());
                locales.add(locale);
            }
        }

        return options;
    }

}
