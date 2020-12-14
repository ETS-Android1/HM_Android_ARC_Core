package com.healthymedium.analytics;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import android.content.Context;

import com.google.gson.JsonObject;
import com.healthymedium.analytics.api.AnalyticsClient;
import com.healthymedium.analytics.models.AppInfo;
import com.healthymedium.analytics.models.HardwareInfo;
import com.healthymedium.analytics.models.OperatingSystemInfo;
import com.healthymedium.analytics.models.StudyInfo;

import java.util.ArrayList;
import java.util.List;

public class Analytics implements LifecycleObserver {

    private static Analytics instance;

    public static final String CRASH = "crash";     // the ship is going down
    public static final String ERROR = "error";     // aberrant behavior but recoverable
    public static final String WARNING = "warn";    // not an error but something to be concerned about
    public static final String INFO = "info";       // something interesting happened
    public static final String DEBUG = "debug";     // extra info during qa

    OperatingSystemInfo os;
    HardwareInfo hardware;
    StudyInfo study;
    AppInfo app;

    CrashHandler crashHandler;
    AnalyticsClient client;

    private Analytics(Context context){
        Config.initialize();
        Log.initialize(context);

        app = new AppInfo(context);
        hardware = new HardwareInfo();
        os = new OperatingSystemInfo(context);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        AnalyticsPreferences.initialize(context);

        crashHandler = new CrashHandler();
        crashHandler.setListener(new CrashHandler.Listener() {
            @Override
            public void onNewReport(Report report) {
                report.setOperatingSystemInfo(os);
                report.setHardwareInfo(hardware);
                report.setStudyInfo(study);
                report.setAppInfo(app);
                client.submitReport(report);
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);

        client = new AnalyticsClient();
        client.consolidateQueue();
        client.trimQueue();
        client.popQueue();
    }

    public static void initialize(Context context){
        instance = new Analytics(context);
    }

    public static void setKey(String key){
        Config.Api.KEY = key;
    }

    public static void setStudyInfo(String participantId, String deviceId){
        instance.study = new StudyInfo(participantId,deviceId);
    }

    public static void crash(){
        throw new RuntimeException("Test Crash");
    }

    public static void uploadLogs(String tag, String message, final Analytics.Listener listener){
        if(instance==null){
            return;
        }

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String stacktrace = parseStacktraceElements(stackTraceElements);

        Report report = new Report(DEBUG,tag,message,stacktrace);
        report.setOperatingSystemInfo(instance.os);
        report.setHardwareInfo(instance.hardware);
        report.setAppInfo(instance.app);

        instance.client.submitLogs(report,listener);
    }

    public static void uploadLogs(String message, final Analytics.Listener listener){
        uploadLogs("Log Upload",message,listener);
    }

    public static void uploadLogs(String message){
        uploadLogs("Log Upload",message,null);
    }

    public static void uploadFile(String message, ParcelFile file, final Analytics.Listener listener) {
        if(instance==null){
            return;
        }

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String stacktrace = parseStacktraceElements(stackTraceElements);

        Report report = new Report(DEBUG,"File Upload",message,stacktrace);
        report.setOperatingSystemInfo(instance.os);
        report.setHardwareInfo(instance.hardware);
        report.setAppInfo(instance.app);

        List<ParcelFile> files = new ArrayList<>();
        files.add(file);

        instance.client.submitParcel(report, files, listener);
    }

    public static void uploadFiles(String message, List<ParcelFile> files, final Analytics.Listener listener) {
        if(instance==null){
            return;
        }

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String stacktrace = parseStacktraceElements(stackTraceElements);

        Report report = new Report(DEBUG,"File Upload",message,stacktrace);
        report.setOperatingSystemInfo(instance.os);
        report.setHardwareInfo(instance.hardware);
        report.setAppInfo(instance.app);

        instance.client.submitParcel(report, files, listener);
    }

    public static void logDebug(String tag, String msg, JsonObject data) {
        log(DEBUG,tag,msg,data);
    }

    public static void logDebug(String tag, String msg) {
        log(DEBUG,tag,msg,null);
    }

    public static void logInfo(String tag, String msg, JsonObject data) {
        log(INFO,tag,msg,data);
    }

    public static void logInfo(String tag, String msg) {
        log(INFO,tag,msg,null);
    }

    public static void logWarning(String tag, String msg, JsonObject data) {
        log(WARNING,tag,msg,data);
    }

    public static void logWarning(String tag, String msg) {
        log(WARNING,tag,msg,null);
    }

    private static void log(String priority, String tag, String msg, JsonObject data) {
        if(instance==null){
            return;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String stacktrace = parseStacktraceElements(stackTraceElements);

        Report report = new Report(priority,tag,msg,stacktrace);
        report.setOperatingSystemInfo(instance.os);
        report.setHardwareInfo(instance.hardware);
        report.setAppInfo(instance.app);
        report.setData(data);
        instance.client.submitReport(report);
    }

    public static void logException(String priority, String tag, Exception e) {
        if(instance==null){
            return;
        }
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String stacktrace = parseStacktraceElements(stackTraceElements);

        Report report = new Report(priority,tag,e.getMessage(),stacktrace);
        report.setOperatingSystemInfo(instance.os);
        report.setHardwareInfo(instance.hardware);
        report.setAppInfo(instance.app);
        instance.client.submitReport(report);
    }

    private static String parseStacktraceElements(StackTraceElement[] elements){
        String stacktrace = new String();
        if(elements.length>4) {
            int last = elements.length-1;
            for (int i = 4; i < elements.length;i++) {
                stacktrace += elements[i].toString();
                if (i!=last) {
                    stacktrace += "\n";
                }
            }
        }
        return stacktrace;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartForeground() {
        app.setVisible(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStopForeground() {
        app.setVisible(false);
    }

    public interface Listener {
        void onSuccess();
        void onFailure();
    }

}
