package com.healthymedium.arc.api.models;

import java.util.List;

public class TestSchedule {

    public String app_version;                  // version of the app
    public String device_info;                  // a string with format
    public String participant_id;               // the user's participant id
    public String device_id;                    // the unique id for this device
    public String model_version = "0";          // the model version of this data object
    public List<TestScheduleSession> sessions;  // an array of objects that define each session

}
