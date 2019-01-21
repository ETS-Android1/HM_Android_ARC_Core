package com.healthymedium.arc.path_data;

import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.PathSegmentData;

public class AvailabilityPathData extends PathSegmentData {

    public boolean weekdaySame; // are all weekday wake and sleep times the same

    public AvailabilityPathData(){
        super();
    }

    @Override
    protected Object onProcess() {
        return null;
    }
}
