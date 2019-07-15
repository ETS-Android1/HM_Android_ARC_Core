package com.healthymedium.arc.hints;

import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import org.joda.time.DateTime;

import java.util.HashMap;

public class Hints {

    private static final String tag = "Hints";
    static HashMap<String, HintDetails> map;

    private Hints(){

    }

    public static void save(){
        PreferencesManager.getInstance().putObject(tag, map.values().toArray());
    }

    public static void load(){
        map = new HashMap<>();

        if(!PreferencesManager.getInstance().contains(tag)){
            return;
        }

        HintDetails[] details = PreferencesManager.getInstance().getObject(tag, HintDetails[].class);
        map = new HashMap<>();
        for (HintDetails detail : details) {
            map.put(detail.name, detail);
        }
    }

    public static boolean hasBeenShown(String key){
        return map.containsKey(key);
    }

    public static boolean hasBeenShownSinceVersion(String key, long versionCode){
        if(!map.containsKey(key)){
            return false;
        }
        HintDetails details = map.get(key);
        return details.versionCode > versionCode;
    }

    public static boolean hasBeenShownSinceDate(String key, DateTime dateTime){
        if(!map.containsKey(key)){
            return false;
        }
        HintDetails details = map.get(key);
        return details.timestamp > (dateTime.getMillis()/1000);
    }

    public static void markShown(String key){
        HintDetails details;

        if(map.containsKey(key)){
            details = map.get(key);
        } else {
            details = new HintDetails();
        }

        details.name = key;
        details.versionCode = VersionUtil.getAppVersionCode();
        details.timestamp = DateTime.now().getMillis()/1000;
        map.put(key,details);
        save();
    }


    public static class HintDetails {
        long versionCode;
        long timestamp;
        String name;
    }

}
