package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class WakeSurvey extends BaseData {

    public List<WakeSurveySection> questions;
    public String type = "wake";
    public Double start_date;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 6;

        for(WakeSurveySection section : questions){
            if(section.display_time==null){
                continue;
            }
            if(section.display_time>0){
                displayCounts++;
            }
        }

        return (int) (100*((float)displayCounts/expectedDisplayCounts));
    }
}
