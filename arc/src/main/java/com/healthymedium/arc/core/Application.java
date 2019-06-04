package com.healthymedium.arc.core;

import android.content.res.Configuration;
import android.util.Log;

import com.healthymedium.arc.study.Study;

import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import net.danlew.android.joda.JodaTimeAndroid;
import java.util.ArrayList;
import java.util.List;

public class Application extends android.app.Application {

    private static final String tag = "Application";
    static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.i(tag,"onCreate");

        VersionUtil.initialize(this);
        JodaTimeAndroid.init(this);
        PreferencesManager.initialize(this);
        CacheManager.initialize(this);
        Device.initialize(this);
        initializeStudy();
    }

    public void initializeStudy() {
        Study.initialize(this);
        registerStudyComponents();
        Study.getInstance().load();
    }

    // register different behaviors here
    public void registerStudyComponents() {
        //Study.getInstance().registerParticipantBehavior();
        //Study.getInstance().registerMigrationBehavior();
        //Study.getInstance().registerSchedulerBehavior();
        //Study.getInstance().registerRestClient();
        //Study.getInstance().registerStudyBehavior();
    }

    // list all locale options offered by the app
    public List<Locale> getLocaleOptions() {
        List<Locale> locales = new ArrayList<>();
        locales.add(new Locale(Locale.COUNTRY_UNITED_STATES,Locale.LANGUAGE_ENGLISH));
        return locales;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(tag,"onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        if(PreferencesManager.getInstance() != null){
            if(PreferencesManager.getInstance().contains("language")){
                String language = PreferencesManager.getInstance().getString("language","en");
                String country = PreferencesManager.getInstance().getString("country","US");
                newConfig.setLocale(new java.util.Locale(language,country));
                getBaseContext().getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
            }
        }
    }

    @Override
    public void onLowMemory() {
        Log.w(tag,"onLowMemory");
        super.onLowMemory();
    }

    public static Application getInstance(){
        return instance;
    }

}
