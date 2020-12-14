package com.healthymedium.analytics;

public class Config {

    public static String FLAVOR_DEV = "dev";
    public static String FLAVOR_QA = "qa";
    public static String FLAVOR_PROD = "prod";

    public static class Api {
        public static final String ENDPOINT_RELEASE = "https://analytics-api.thinkhealthymedium.com";
        public static final String ENDPOINT_DEBUG = "https://analytics-qa-api.thinkhealthymedium.com";
        public static final String VERSION = "1";

        public static String ENDPOINT = "";
        public static String KEY = ""; // needs to be set in the app before initialize is called
        public static boolean ENABLED = true;
    }

    public static class Log {
        public static final String VERSION = "1";
        public static boolean SAVE_DEBUG_OUTPUT = false;
    }

    // ---------------------------------------------------------------------------------------------

    public static class Queue {
        public static int MAX_SIZE = 64;
    }

    // ---------------------------------------------------------------------------------------------

    public static void initialize() {
        // set the endpoint based on build config
        Api.ENDPOINT = (BuildConfig.DEBUG) ? Api.ENDPOINT_DEBUG:Api.ENDPOINT_RELEASE;
        // if no key is available, disable api
        if(Api.KEY.isEmpty()){
            Api.ENABLED = false;
        }
        // if qa build, save debug output
        if(BuildConfig.FLAVOR.equals(FLAVOR_QA)){
            Log.SAVE_DEBUG_OUTPUT = true;
        }
    }

}
