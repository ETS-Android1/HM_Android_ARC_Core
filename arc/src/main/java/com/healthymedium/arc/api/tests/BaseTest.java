package com.healthymedium.arc.api.tests;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class BaseTest {
    protected String type;

    public BaseTest(){
        type = "default";
    }

    public void load(List<Object> segments){
        Field[] fields = getClass().getFields();
        for(int i=0;i<fields.length;i++){
            if( Modifier.isPublic(fields[i].getModifiers())){
                for(Object segment : segments){
                    Class segmentClass = segment.getClass();
                    Class fieldClass = fields[i].getType();
                    if(fieldClass==segmentClass){
                        Log.i("Data Routing","field match found");
                        try {
                            fields[i].setAccessible(true);
                            fields[i].set(this,segment);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
