package com.healthymedium.test_suite.core;

import com.healthymedium.test_suite.events.EventBase;
import com.healthymedium.arc.core.Device;

import org.joda.time.DateTime;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestReport {

    private Description description;
    private String id;

    // device
    private String deviceId;
    private String deviceInfo;
    private String deviceName;

    private List<EventBase> events = new ArrayList<>();

    public TestReport(Description desc){
        id = UUID.randomUUID().toString();

        description = desc;
        deviceId = Device.getId();
        deviceInfo = Device.getInfo();
        deviceName = Device.getName();
    }

    void addEvent(EventBase event){
        events.add(event);
    }

}
