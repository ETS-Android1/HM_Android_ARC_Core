package com.healthymedium.test_suite.events;

public class GenericEvent extends EventBase {

    public GenericEvent(String tag, String msg) {
        super(EventTypes.GENERIC,tag, msg);
    }

}
