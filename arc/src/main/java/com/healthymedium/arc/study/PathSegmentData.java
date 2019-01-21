package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.api.RestClient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathSegmentData {

    protected List<Object> objects = new ArrayList<>();

    public void add(Object object) {
        objects.add(object);
    }


    public Object process() {
        Object object = onProcess();
        return object;
    }

    // Override this
    protected Object onProcess() {
        return null;
    }

    protected <T> T processHashMap(Map<String,Object> map, Class<T> clss) {
        if (clss == null || map == null) {
            return null;
        }

        T result = null;

        try {
            result = (T) clss.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        Field[] fields = clss.getFields();
        for (int i = 0; i < fields.length; i++) {
            String key = fields[i].getName();
            if (map.containsKey(key)) {
                Object object = map.get(key);
                Class objectClass = object.getClass();
                Class fieldClass = fields[i].getType();
                if (fieldClass == objectClass || fieldClass==Object.class) {
                    try {
                        fields[i].setAccessible(true);
                        fields[i].set(result, object);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

}