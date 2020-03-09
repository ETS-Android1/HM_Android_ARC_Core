package com.healthymedium.arc.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Locale {

    private static final String TAG = Locale.class.getSimpleName();

    //SharedPreference keys
    public static final String TAG_LANGUAGE = "localeLanguage";
    public static final String TAG_COUNTRY = "localeCountry";

    //Supported locales
    private static final java.util.Locale US                = java.util.Locale.US;
    private static final java.util.Locale CANADA            = java.util.Locale.CANADA;
    private static final java.util.Locale CANADA_FRENCH     = java.util.Locale.CANADA_FRENCH;
    private static final java.util.Locale MEXICO            = new java.util.Locale("es", "MX");
    private static final java.util.Locale SPAIN             = new java.util.Locale("es", "ES");
    private static final java.util.Locale GERMANY           = java.util.Locale.GERMANY;
    private static final java.util.Locale JAPAN             = java.util.Locale.JAPAN;
    private static final java.util.Locale AUSTRALIA         = new java.util.Locale("en", "AU");
    private static final java.util.Locale UK                = new java.util.Locale("en", "GB");

    private static final List<java.util.Locale> supported;
    private static final Map<String, String> labels;

    //Default locale
    private static final java.util.Locale defaultLocale = US;
    private static java.util.Locale currentLocale = defaultLocale;

    static {
        supported = new ArrayList<>();
        labels = new HashMap<>();

        //Initialize supported locales
        supported.add(MEXICO);
        supported.add(AUSTRALIA);
        supported.add(CANADA);
        supported.add(CANADA_FRENCH);
        supported.add(GERMANY);
        supported.add(SPAIN);
        supported.add(JAPAN);
        supported.add(UK);
        supported.add(US);

        //Initialize labels
        labels.put("es_MX", "America Latina - Español");
        labels.put("en_AU", "Australia - English");
        labels.put("en_CA", "Canada - English");
        labels.put("fr_CA", "Canada - Français");
        labels.put("de_DE", "Deutschland - Deutsche");
        labels.put("es_ES", "Europa - Español");
        labels.put("ja_JP", "日本 - 日本語");
        labels.put("en_GB", "United Kingdom - English");
        labels.put("en_US", "United States - English");
    }

    public static String getCountry() {
        return currentLocale.getCountry();
    }

    public static String getLanguage() {
        return currentLocale.getLanguage();
    }

    public static String getLabel() {
        return getLabel(currentLocale);
    }

    public static String getCountry(final java.util.Locale locale) {
        return locale.getCountry();
    }

    public static String getLanguage(final java.util.Locale locale) {
        return locale.getLanguage();
    }

    public static String getLabel(java.util.Locale locale) {
        String key = locale.getLanguage() + "_" + locale.getCountry();
        return labels.get(key);
    }

    public static String getKey() {
        return currentLocale.getLanguage() + "_" + currentLocale.getCountry();
    }


    public static List<java.util.Locale> getSupported() {
        return new ArrayList<>(supported);
    }

    public static java.util.Locale getDefault() {
        return new java.util.Locale(defaultLocale.getLanguage(), defaultLocale.getCountry());
    }

    public static List<String> getOptionList() {
        List<String> options = new ArrayList<>(supported.size());

        for(java.util.Locale locale : supported) {
            options.add(getLabel(locale));
        }

        return options;
    }

    public static java.util.Locale getCurrent() {
        return new java.util.Locale(currentLocale.getLanguage(), currentLocale.getCountry());
    }

    public static java.util.Locale getLocaleFromPreferences(Context context) {
        if(context == null) {
            Log.d(TAG, "Couldn't fetch locale from shared preferences (null) context");
            return defaultLocale;
        }
        String language = PreferencesManager.getInstance().getString(TAG_LANGUAGE, currentLocale.getLanguage());
        String country = PreferencesManager.getInstance().getString(TAG_COUNTRY, currentLocale.getCountry());

        return new java.util.Locale(language, country);
    }

    public static void update(final java.util.Locale locale,  Context context) {
        //Guard statements
        if(context == null) {
            Log.d(TAG, "Locale not set, (null) context");
            return;
        }

        PreferencesManager preferences = PreferencesManager.getInstance();
        if(preferences == null) {
            Log.d(TAG, "Locale not set, (null) PreferenceManager");
            return;
        }

        if(locale == null) {
            Log.d(TAG, "Locale not set, (null) locale");
            return;
        }

        //Set the locale and update config
        final Resources resources = context.getResources();
        final Configuration config = resources.getConfiguration();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        config.setLocale(locale);
        resources.updateConfiguration(config, displayMetrics);

        currentLocale = locale;

        //Update shared preferences
        preferences.putString(TAG_COUNTRY, getCountry());
        preferences.putString(TAG_LANGUAGE, getLanguage());
    }

    /** Synchronize locale stored in the application configuration with the
     * locale stored in shared preferences. **/
    public static void sync(Context context) {
        if(context == null) {
            Log.d(TAG, "Not synced, (null) context");
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName()+".prefs", Context.MODE_PRIVATE);

        if(prefs == null) {
            Log.d(TAG, "Not synced, (null) SharedPreferences reference");
            return;
        }

        String language = prefs.getString(Locale.TAG_LANGUAGE, "null");
        String country = prefs.getString(Locale.TAG_COUNTRY, "null");

        if(language.equals("null") || country.equals("null")) {
            return;
        }

        currentLocale = new java.util.Locale(language, country);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        config.setLocale(currentLocale);
        resources.updateConfiguration(config, displayMetrics);
    }

}
