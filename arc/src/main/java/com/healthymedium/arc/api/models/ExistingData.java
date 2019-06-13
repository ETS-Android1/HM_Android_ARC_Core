package com.healthymedium.arc.api.models;

public class ExistingData {

    public SessionInfo first_test;
    public SessionInfo latest_test;
    public WakeSleepSchedule wake_sleep_schedule;
    public TestSchedule test_schedule;


    public boolean isValid(){
        if(first_test==null){
            return false;
        }
        if(latest_test==null){
            return false;
        }
        if(wake_sleep_schedule==null){
            return false;
        }
        if(test_schedule==null){
            return false;
        }
        return true;
    }

}
