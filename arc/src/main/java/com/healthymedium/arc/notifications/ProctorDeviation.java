package com.healthymedium.arc.notifications;

import com.healthymedium.arc.utilities.CacheManager;

import org.joda.time.DateTime;

public class ProctorDeviation {

    DateTime lastIncident;
    DateTime lastRequest;

    public ProctorDeviation(){
        lastIncident = null;
        lastRequest = null;
    }

    public void markIncident(){
        lastIncident = DateTime.now();
    }

    public void markRequest(){
        lastRequest = DateTime.now();
    }

    public boolean save(){
        CacheManager cache = CacheManager.getInstance();
        if(cache==null) {
            return false;
        }
        if(!cache.putObject("ProctorDeviation", this)){
            return false;
        }
        return cache.save("ProctorDeviation");
    }

    public static ProctorDeviation load(){
        CacheManager cache = CacheManager.getInstance();
        if(cache==null) {
            return new ProctorDeviation();
        }
        return cache.getObject("ProctorDeviation", ProctorDeviation.class);
    }

    public static boolean shouldRequestBeMade(){
        ProctorDeviation deviation = load();

        if(deviation.lastIncident==null){
            return false;
        }
        if(deviation.lastRequest==null){
            return true;
        }
        if(deviation.lastRequest.isAfter(deviation.lastIncident)){
            return false;
        }
        return deviation.lastRequest.plusHours(24).isBeforeNow();
    }

}
