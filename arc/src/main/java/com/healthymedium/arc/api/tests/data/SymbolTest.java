package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class SymbolTest extends BaseData {

    public Double date;
    public List<SymbolTestSection> sections;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 12;

        for(SymbolTestSection section : sections){
            if(section.appearanceTime==null){
                continue;
            }
            if(section.appearanceTime>0){
                displayCounts++;
            }
        }

        return (int) (100*((float)displayCounts/expectedDisplayCounts));
    }

}
