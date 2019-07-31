package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class ChronotypeSurvey extends BaseData {

    public List<ChronotypeSurveySection> questions;
    public String type = "chronotype";
    public Double start_date;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 6;

        for(ChronotypeSurveySection section : questions){
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
