package com.healthymedium.arc.core;

import android.content.Context;
import android.content.res.Configuration;

import com.healthymedium.arc.library.R;

public class Locale {

    public static final String TAG_LANGUAGE = "localeLanguage";
    public static final String TAG_COUNTRY = "localeCountry";

    public static String COUNTRY_ARGENTINA = "AR";
    public static String COUNTRY_AUSTRALIA = "AU";
    public static String COUNTRY_BRAZIL = "BR";
    public static String COUNTRY_CANADA = "CA";
    public static String COUNTRY_COLUMBIA = "CO";
    public static String COUNTRY_GERMANY = "DE";
    public static String COUNTRY_SPAIN = "ES";
    public static String COUNTRY_FRANCE = "FR";
    public static String COUNTRY_UNITED_KINGDOM = "GB";
    public static String COUNTRY_IRELAND = "IE";
    public static String COUNTRY_ITALY = "IT";
    public static String COUNTRY_MEXICO = "MX";
    public static String COUNTRY_NETHERLANDS = "NL";
    public static String COUNTRY_UNITED_STATES = "US";
    public static String COUNTRY_EUROPE = "EU";
    public static String COUNTRY_JAPAN = "JP";
    public static String COUNTRY_KOREA = "KR";

    public static String LANGUAGE_GERMAN = "de";
    public static String LANGUAGE_ENGLISH = "en";
    public static String LANGUAGE_SPANISH = "es";
    public static String LANGUAGE_FRENCH = "fr";
    public static String LANGUAGE_ITALIAN = "it";
    public static String LANGUAGE_DUTCH = "nl";
    public static String LANGUAGE_PORTUGUESE = "pt";
    public static String LANGUAGE_JAPANESE = "ja";
    public static String LANGUAGE_KOREAN = "ko";

    private String country;
    private String language;
    private boolean fullySupported;

    public Locale(boolean fullySupported, String language, String country){
        this.fullySupported = fullySupported;
        this.country = country;
        this.language = language;
    }

    public Locale(boolean fullySupported, String language){
        this.fullySupported = fullySupported;
        this.country = "";
        this.language = language;
    }

    public String getLabel() {
        Context context = Application.getInstance().getAppContext();
        Configuration appConfig = context.getResources().getConfiguration();

        Configuration localeConfig = new Configuration(appConfig);
        localeConfig.setLocale(new java.util.Locale(language,country));

        Context localeContext = context.createConfigurationContext(localeConfig);
        String label = localeContext.getString(R.string.key_native_language);

        return label;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public boolean IsfullySupported() {
        return fullySupported;
    }

    public java.util.Locale getNativeObj() {
        return new java.util.Locale(language,country);
    }

}
