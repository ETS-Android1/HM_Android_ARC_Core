package com.healthymedium.arc.utilities;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.log.BehaviorLog;
import com.healthymedium.arc.log.InternalLog;
import com.healthymedium.arc.log.SystemLog;

import java.io.File;

public class Log {

    public static BehaviorLog behavior;
    public static SystemLog system;
    static InternalLog internal;

    public static void init() {
        File file = new File(Application.getInstance().getCacheDir()+"/log");
        if(!file.exists()) {
            file.mkdir();
        }
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
        return internal.getFilename();
    }

    // add other methods if required...
}