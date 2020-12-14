package com.healthymedium.test_suite.events;

import org.joda.time.DateTime;

public class EventBase {
    private DateTime timestamp;
    protected String type;
    protected String tag;
    protected String msg;

    public EventBase(String type, String tag, String msg){
        timestamp = DateTime.now();
        this.type = type;
        this.msg = msg;
        this.tag = tag;
    }
}