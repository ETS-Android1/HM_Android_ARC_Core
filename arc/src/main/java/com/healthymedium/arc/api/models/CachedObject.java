package com.healthymedium.arc.api.models;

import android.content.Intent;

import com.healthymedium.arc.utilities.CacheManager;

import java.io.File;
import java.lang.reflect.Field;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CachedObject {

    public String filename;
    public String mediaType;

    public RequestBody getRequestBody(){

        File object = CacheManager.getInstance().getFile(filename);
        RequestBody uploadFile = RequestBody.create(MediaType.parse(mediaType),object);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file",filename,uploadFile);

        Field[] fields = getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            String key = fields[i].getName();
            String value = fieldToString(fields[i]);
            if(value.length()>0) {
                builder.addFormDataPart(key, value);
            }
        }

        return builder.build();
    }


    public String fieldToString(Field field) {
        String name = field.getName();
        if(name.equals("serialVersionUID")){
            return "";
        }
        if(name.equals("filename")){
            return "";
        }
        if(name.equals("mediaType")){
            return "";
        }

        Class fieldClass = field.getType();
        Object value = null;
        try {
            field.setAccessible(true);

            value = field.get(this);

            /*
            if(fieldClass.equals(String.class)){
                value = field.get(new String());
            } else if (fieldClass.equals(Integer.class)) {
                value = field.get(new Integer(0));
            } else if (fieldClass.equals(Long.class)) {
                value = field.get(new Long(0));
            } else if (fieldClass.equals(Double.class)) {
                value = field.get(new Double(0));
            } else if (fieldClass.equals(Float.class)) {
                value = field.get(new Float(0));
            }
            */

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(value==null){
            return "";
        }

        return String.valueOf(value);
    }
}
