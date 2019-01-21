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

    //Debug
    public static boolean DEBUG_DIALOGS = true; // click the header on most screens a couple times and a debug dialog will appear

    // Runtime
    public static boolean OPENED_FROM_NOTIFICATION = false; //

}
