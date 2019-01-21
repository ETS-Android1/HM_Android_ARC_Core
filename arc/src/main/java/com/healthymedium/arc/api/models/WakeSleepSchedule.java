package com.healthymedium.arc.api.models;

import java.util.List;

public class WakeSleepSchedule {

    public String app_version;          // "app_version" : (string, version of the app),
    public String device_id;            // "device_id" : (string, the unique id for this device),
    public String device_info;          // "device_info": (a string with format "OS name|device model|OS version", ie "iOS|iPhone8,4|10.1.1")
    public String participant_id;       // "participant_id" : (string, the user's participant id),

    public List<WakeSleepData> wakeSleepData;
    public String model_version = "0";
}
