package com.healthymedium.arc.path_data;

import com.healthymedium.arc.study.PathSegmentData;

public class SinglePageData extends PathSegmentData {


    public SinglePageData(){
        super();

    }

    @Override
    protected Object onProcess() {
        if(objects.size()>0){
            return objects.get(0);
        }
        return null;
    }
}
