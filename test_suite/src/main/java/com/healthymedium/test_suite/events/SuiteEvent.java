package com.healthymedium.test_suite.events;

import org.junit.runner.Description;

public class SuiteEvent extends EventBase {

    String error;

    public SuiteEvent(String tag, Description description, String error) {
        super(EventTypes.SUITE,tag, description.getDisplayName());
        this.error = error;
    }

    public SuiteEvent(String tag, Description description) {
        super(EventTypes.SUITE,tag, description.getDisplayName());
    }

    public SuiteEvent(String tag, String msg) {
        super(EventTypes.SUITE,tag, msg);
    }

}
