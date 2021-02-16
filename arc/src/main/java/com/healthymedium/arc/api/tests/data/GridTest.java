package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class GridTest extends BaseData {

    public Double date;
    public List<GridTestSection> sections;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 2;

        for(GridTestSection section : sections){
            if(section.displayTestGrid==null){
                continue;
            }
            if(section.displayTestGrid>0){
                displayCounts++;
            }
        }

        return (int) (100*((float)displayCounts/expectedDisplayCounts));
    }

}
