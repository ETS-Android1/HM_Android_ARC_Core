package com.healthymedium.analytics;

import android.content.Context;

import com.healthymedium.analytics.log.BehaviorLog;
import com.healthymedium.analytics.log.InternalLog;
import com.healthymedium.analytics.log.SystemLog;

import java.io.File;

public class Log {

    public static BehaviorLog behavior;
    public static SystemLog system;
    public static InternalLog internal;
    static String dirLog;

    public static void initialize(Context context) {
        dirLog = context.getCacheDir()+"/log";
        File file = new File(dirLog);
        if(!file.exists()) {
            file.mkdir();
        }

        behavior = new BehaviorLog(context);
        system = new SystemLog(context);
        internal = new InternalLog(context);
    }

    public static void initialize() {
        behavior = new BehaviorLog();
        system = new SystemLog();
        internal = new InternalLog();
    }

    public static void v(String tag, String msg) {
        internal.v(tag,msg);
    }

    public static void d(String tag, String msg) {
        internal.d(tag,msg);
    }

    public static void i(String tag, String msg) {
        internal.i(tag,msg);
    }

    public static void w(String tag, String msg) {
        internal.w(tag,msg);
    }

    public static void e(String tag, String msg) {
        internal.e(tag,msg);
    }

    public static void wtf(String tag, String msg) {
        internal.wtf(tag,msg);
    }

    public static void pointToLogcat(){
        internal.pointToLogcat();
    }

    public static void pointToSystemOut(){
        internal.pointToSystemOut();
    }

    public static String filename() {
        return internal.filename();
    }

    public static String directory() {
        return dirLog;
    }

    // add other methods if required...
}