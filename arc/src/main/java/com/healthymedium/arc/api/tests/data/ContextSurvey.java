package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class ContextSurvey extends BaseData {

    public List<ContextSurveySection> questions;
    public String type = "context";
    public Double start_date;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 5;

        for(ContextSurveySection section : questions){
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
