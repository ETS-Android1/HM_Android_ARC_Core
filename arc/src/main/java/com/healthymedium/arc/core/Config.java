package com.healthymedium.arc.core;

public class Config {

    public static String FLAVOR_DEV = "dev";
    public static String FLAVOR_QA = "qa";
    public static String FLAVOR_PROD = "prod";

    // Core
    public static boolean CHOOSE_LOCALE = false;

    // Rest API
    public static String REST_ENDPOINT = "http://thinkhealthymedium.com/"; // where we send the data
    public static boolean REST_BLACKHOLE = false; // used for debugging, keeps all rest calls from reaching the outside world
    public static boolean REST_HEARTBEAT = true; // heartbeat will fail if blackhole is enabled
    public static boolean CHECK_SESSION_INFO = false; // if true, an api is called after registration to check for existing session info
    public static boolean CHECK_CONTACT_INFO = false; // if true, an api is called after registration to check for contact info

    //
    public static boolean ENABLE_SIGNATURES = false; // if true, signatures will be required before and after every test
    public static boolean ENABLE_VIGNETTES = false; // if true, a notification reminding the user of the upcoming visit will appear one month, week, and day from the start date
    public static boolean ENABLE_LEGACY_PRICE_SETS = false; // if true, the PriceManager will continue to use a long-standing, but incorrect, method to determine the price set for a given test session.
    public static boolean IS_REMOTE = false; // if true, user will be required to state whether or not they commit to the study
    public static boolean EXPECTS_2FA_TEXT = true; // if true, user will receive a 2FA text message, if false use site code
    //Debug
    public static boolean DEBUG_DIALOGS = false; // click the header on most screens a couple times and a debug dialog will appear
    public static boolean ENABLE_LOGGING = false; // save logcat output to cached file 'Log'

    // Runtime
    public static final String INTENT_EXTRA_OPENED_FROM_NOTIFICATION = "OPENED_FROM_NOTIFICATION";
    public static boolean OPENED_FROM_NOTIFICATION = false;

    public static final String INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION = "OPENED_FROM_VISIT_NOTIFICATION";
    public static boolean OPENED_FROM_VISIT_NOTIFICATION = false;

}
