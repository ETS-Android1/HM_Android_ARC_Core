package com.healthymedium.arc.core;

public class Locale {

    public static String COUNTRY_AUSTRALIA = "AU";
    public static String COUNTRY_CANADA = "CA";
    public static String COUNTRY_SPAIN = "ES";
    public static String COUNTRY_FRANCE = "FR";
    public static String COUNTRY_UNITED_KINGDOM = "GB";
    public static String COUNTRY_UNITED_STATES = "US";

    public static String LANGUAGE_ENGLISH = "en";
    public static String LANGUAGE_FRENCH = "CA";
    public static String LANGUAGE_SPANISH = "es";

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
            return "United States - English";
        } else {
            return "";
        }
    }

    private String getFrenchLabel(String country){
        if(country==COUNTRY_FRANCE){
            return "France - Francais";
        } else if(country==COUNTRY_CANADA){
            return "Canada - Francais";
        } else {
            return "";
        }
    }

    private String getSpanishLabel(String country){
        if(country==COUNTRY_SPAIN){
            return "España - Español";
        } else if(country==COUNTRY_UNITED_STATES){
            return "United States - Español";
        } else {
            return "";
        }
    }
}
