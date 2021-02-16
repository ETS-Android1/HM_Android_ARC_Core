package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class PriceTest extends BaseData {

    public Double date;
    public List<PriceTestSection> sections;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 10;

        for(PriceTestSection section : sections){
            if(section.questionDisplayTime==null){
                continue;
            }
            if(section.questionDisplayTime>0){
                displayCounts++;
            }
        }

        return (int) (100*((float)displayCounts/expectedDisplayCounts));
    }


}
