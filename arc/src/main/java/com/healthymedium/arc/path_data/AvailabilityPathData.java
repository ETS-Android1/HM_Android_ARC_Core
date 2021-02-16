package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.study.PathSegmentData;

public class AvailabilityPathData extends PathSegmentData {

    public boolean weekdaySame; // are all weekday wake and sleep times the same

    public AvailabilityPathData(){
        super();
    }

    @Override
    protected BaseData onProcess() {
        return null;
    }
}
