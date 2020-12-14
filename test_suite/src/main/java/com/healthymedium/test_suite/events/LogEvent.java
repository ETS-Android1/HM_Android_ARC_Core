package com.healthymedium.test_suite.events;

public class LogEvent extends EventBase {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;

    // ---------------------------------------------------------------------------------------------

    int priority;

    public LogEvent(int priority, String tag, String msg) {
        super(EventTypes.LOG, tag, msg);
    }

}
