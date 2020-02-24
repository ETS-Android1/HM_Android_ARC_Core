package com.healthymedium.arc.core;

public class Locale {

    public static final String TAG_LANGUAGE = "localeLanguage";
    public static final String TAG_COUNTRY = "localeCountry";

    public static String COUNTRY_ARGENTINA = "AR";
    public static String COUNTRY_AUSTRALIA = "AU";
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
    public static String COUNTRY_AMERICA_LATINA = "AL";

    public static String LANGUAGE_GERMAN = "de";
    public static String LANGUAGE_ENGLISH = "en";
    public static String LANGUAGE_SPANISH = "es";
    public static String LANGUAGE_FRENCH = "fr";
    public static String LANGUAGE_ITALIAN = "it";
    public static String LANGUAGE_DUTCH = "nl";
    public static String LANGUAGE_JAPANESE = "ja";

    private String label;
    private String country;
    private String language;


    public Locale(String country, String language, String label){
        this.label = label;
        this.country = country;
        this.language = language;
    }

    public Locale(String country, String language){
        if(language==LANGUAGE_ENGLISH){
            label = getEnglishLabel(country);
        } else if(language==LANGUAGE_FRENCH){
            label = getFrenchLabel(country);
        } else if(language==LANGUAGE_SPANISH){
            label = getSpanishLabel(country);
        } else if(language==LANGUAGE_GERMAN) {
            label =  getGermanLabel(country);
        } else if(language==LANGUAGE_DUTCH) {
            label = getDutchLabel(country);
        } else if(language==LANGUAGE_ITALIAN) {
            label = getItalianLabel(country);
        } else if(language==LANGUAGE_JAPANESE) {
            label = getJapaneseLabel(country);
        } else {
            label = "";
        }

        this.country = country;
        this.language = language;
    }

    public String getLabel() {
        return label;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    private String getEnglishLabel(String country){
        if(country==COUNTRY_AUSTRALIA){
            return "Australia - English";
        } else if(country==COUNTRY_CANADA){
            return "Canada - English";
        } else if(country==COUNTRY_UNITED_KINGDOM){
            return "United Kingdom - English";
        } else if(country==COUNTRY_UNITED_STATES){
            return "US - English";
        } else if(country==COUNTRY_IRELAND) {
            return "Ireland - English";
        } else {
            return "";
        }
    }

    private String getFrenchLabel(String country){
        if(country==COUNTRY_FRANCE){
            return "France - Français";
        } else if(country==COUNTRY_CANADA){
            return "Canada - Français";
        } else {
            return "";
        }
    }

    private String getSpanishLabel(String country){
        if(country==COUNTRY_SPAIN){
            return "España - Español";
        } else if(country==COUNTRY_UNITED_STATES){
            return "United States - Español";
        } else if(country==COUNTRY_COLUMBIA){
            return "Columbia - Español";
        } else if(country==COUNTRY_ARGENTINA){
            return "Argentina - Español";
        } else if(country==COUNTRY_MEXICO){
            return "Mexico - Español";
        } else if(country==COUNTRY_EUROPE){
            return "Europa - Español";
        } else if(country==COUNTRY_AMERICA_LATINA){
            return "America Latina - Español";
        } else {
            return "";
        }
    }

    private String getGermanLabel(String country){
        if(country==COUNTRY_GERMANY){
            return "Deutschland - Deutsche";
        } else {
            return "";
        }
    }

    private String getDutchLabel(String country){
        if(country==COUNTRY_NETHERLANDS){
            return "Nederland - Nederlands";
        } else {
            return "";
        }
    }

    private String getItalianLabel(String country){
        if(country==COUNTRY_ITALY){
            return "Italia - Italiano";
        } else {
            return "";
        }
    }

    private String getJapaneseLabel(String country){
        if(country==COUNTRY_JAPAN){
            return "日本 - 日本語";
        } else {
            return "";
        }
    }
}
