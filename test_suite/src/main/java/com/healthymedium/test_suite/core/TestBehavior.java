package com.healthymedium.test_suite.core;

import com.healthymedium.arc.core.Locale;
import com.healthymedium.test_suite.behaviors.Behavior;

import java.util.List;
import java.util.Map;

public class TestBehavior {

    public static final String TAG_ = "";

    public static Map<Class, Behavior> screenMap;

    public static List<Locale> locales;

    public static String testName;

    public static int STEPS = 0;

    public static class login {
        public static String id;
        public static String idConfirm;
        public static String authCode;
    }

    public static class availability {
        public static String wake;
        public static String bed;
    }

    public static class state {
        public static int path;
        public static int lifecycle;
    }

    // needed to override classes
    public static class classes {
        public static Class activity;
        public static Class stateMachine;
        public static Class participant;
        public static Class scheduler;
        public static Class restClient;
        public static Class restApi;
    }

}
