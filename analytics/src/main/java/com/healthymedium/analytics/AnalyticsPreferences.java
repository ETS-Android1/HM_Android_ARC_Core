package com.healthymedium.analytics;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

public class AnalyticsPreferences {

    private static final String tag = "AnalyticsPreferences";

    private static AnalyticsPreferences instance;
    private static SharedPreferences sharedPreferences;
    private static Gson objectGson;

    private AnalyticsPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("com.healthymedium.analytics.prefs", Context.MODE_PRIVATE);
        buildObjectGson();
    }

    public static synchronized void initialize(Context context) {
        instance = new AnalyticsPreferences(context);
    }

    private void buildObjectGson(){
        objectGson = new GsonBuilder()
                .create();
    }

    public static boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public static void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public static void removeAll() {
        sharedPreferences.edit().clear().commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public static double getDouble(String key, double defValue) {
        if(contains(key)){
            return getObject(key, double.class);
        }
        return defValue;
    }

    public static int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }
    
    public static void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static void putDouble(String key, double value) {
        putObject(key,value);
    }

    public static void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).commit();
    }

    public static void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public static void putString(String key, String value) {
        if(value==null){
            Log.i(tag,"invalid string, failed to save");
            throw new RuntimeException("tried to save a null string");
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static void putObject(String key, Object object) {
        if(object==null){
            Log.i(tag,"invalid object, failed to save");
            throw new RuntimeException("tried to save a null object");
        }
        String json = objectGson.toJson(object);
        sharedPreferences.edit().putString(key, json).commit();
    }

    public static <T> T getObject(String key, Class<T> clazz) {
        String json = sharedPreferences.getString(key, "{}");
        T object = null;

        try {
            object = objectGson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            Log.system.e(tag,"failed to load object '"+key+"'");
            Log.system.e(tag,e.getMessage());
        }
        return object;
    }

    public static <T> T getObject(String key, Type type) {
        String json = sharedPreferences.getString(key, "{}");
        T object = null;
        try {
            object = objectGson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            Log.system.e(tag,"failed to load object '"+key+"'");
            Log.system.e(tag,e.getMessage());
        }

        return object;
    }

}
