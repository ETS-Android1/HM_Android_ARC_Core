package com.healthymedium.analytics;

import com.google.gson.JsonObject;
import com.healthymedium.analytics.models.AppInfo;
import com.healthymedium.analytics.models.HardwareInfo;
import com.healthymedium.analytics.models.OperatingSystemInfo;
import com.healthymedium.analytics.models.StudyInfo;
import com.healthymedium.analytics.models.Version;

import java.util.ArrayList;
import java.util.List;

public class Report {

    JsonObject data;

    AppInfo app;
    StudyInfo study;
    HardwareInfo hardware;
    OperatingSystemInfo os;

    String stacktrace;
    String priority;
    String message;
    String tag;

    List<Long> timestamps = new ArrayList<>();
    Version version = new Version();

    public Report(String priority, String tag, String message, String stacktrace){
        long timestamp = System.currentTimeMillis();
        timestamps.add(timestamp);

        this.tag = tag;
        this.priority = priority;
        this.message = message;
        this.stacktrace = stacktrace;
    }

    public Report(Throwable throwable){
        long timestamp = System.currentTimeMillis();
        timestamps.add(timestamp);

        priority = Analytics.CRASH;
        message = throwable.getClass().getSimpleName()+" : "+throwable.getMessage();
        stacktrace = parseStacktraceElements(throwable.getStackTrace());
    }

    private static String parseStacktraceElements(StackTraceElement[] elements){
        String stacktrace = new String();
        if(elements.length>0) {
            int last = elements.length-1;
            for (int i = 0; i < elements.length;i++) {
                stacktrace += elements[i].toString();
                if (i!=last) {
                    stacktrace += "\n";
                }
            }
        }
        return stacktrace;
    }

    public void setData(JsonObject object){
        data = object;
    }

    public void setAppInfo(AppInfo info){
        app = info;
    }

    public void setStudyInfo(StudyInfo info){
        study = info;
    }

    public void setHardwareInfo(HardwareInfo info){
        hardware = info;
    }

    public void setOperatingSystemInfo(OperatingSystemInfo info){
        os = info;
    }

    public String getStacktrace(){
        return stacktrace;
    }

    public List<Long> getInstances(){
        return timestamps;
    }

    public void addInstances(List<Long> instances){
        timestamps.addAll(instances);
    }

}
