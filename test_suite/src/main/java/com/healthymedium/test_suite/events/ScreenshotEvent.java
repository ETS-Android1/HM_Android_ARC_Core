package com.healthymedium.test_suite.events;

public class ScreenshotEvent extends EventBase {

    public ScreenshotEvent(String tag, String msg) {
        super(EventTypes.SUITE,tag, msg);
    }

    public ScreenshotEvent(String tag) {
        super(EventTypes.SCREENSHOT,tag, "");
    }

}
