package com.healthymedium.arc.core;

public class Config {

    public static String FLAVOR_DEV = "dev";
    public static String FLAVOR_QA = "qa";
    public static String FLAVOR_PROD = "prod";

    // Core
    public static boolean CHOOSE_LOCALE = false;

    // Rest API
    public static String REST_ENDPOINT = "http://thinkhealthymedium.com/"; // where we send the data
    public static boolean REST_BLACKHOLE = true; // used for debugging, keeps all rest calls from reaching the outside world
    public static boolean REST_HEARTBEAT = false; // heartbeat will fail if blackhole is enabled
    public static boolean CHECK_SESSION_INFO = false; // if true, an api is called after registration to check for existing session info
    public static boolean CHECK_CONTACT_INFO = false; // if true, an api is called after registration to check for contact info

    //Debug
    public static boolean DEBUG_DIALOGS = true; // click the header on most screens a couple times and a debug dialog will appear

    // Runtime
    public static final String INTENT_EXTRA_OPENED_FROM_NOTIFICATION = "OPENED_FROM_NOTIFICATION";
    public static boolean OPENED_FROM_NOTIFICATION = false;

    public static final String INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION = "OPENED_FROM_VISIT_NOTIFICATION";
    public static boolean OPENED_FROM_VISIT_NOTIFICATION = false;

}
