package com.healthymedium.analytics.log;

import android.content.Context;

import com.healthymedium.analytics.Config;

public class InternalLog extends BaseLog {

    private boolean logSystemOut = false;

    public InternalLog(Context context) {
        super(context, "debug.log",true);
        if(Config.Log.SAVE_DEBUG_OUTPUT){
            write("--------------------\n");
        }
    }

    public InternalLog() {
        super();
        logSystemOut = true;
    }

    public void v(String tag, String msg) {
        if(Config.Log.SAVE_DEBUG_OUTPUT){
            write("V",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("V",tag,msg);
            return;
        }
        android.util.Log.v(tag,msg);
    }

    public void d(String tag, String msg) {
        if(Config.Log.SAVE_DEBUG_OUTPUT){
            write("D",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("D",tag,msg);
            return;
        }
        android.util.Log.d(tag,msg);
    }

    public void i(String tag, String msg) {
        if(Config.Log.SAVE_DEBUG_OUTPUT){
            write("I",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("I",tag,msg);
            return;
        }
        android.util.Log.i(tag,msg);

    }

    public void w(String tag, String msg) {
        if(Config.Log.SAVE_DEBUG_OUTPUT){
            write("W",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("W",tag,msg);
            return;
        }
        android.util.Log.w(tag,msg);
    }

    public void e(String tag, String msg) {
        if(Config.Log.SAVE_DEBUG_OUTPUT){
            write("E",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("E",tag,msg);
            return;
        }
        android.util.Log.e(tag,msg);
    }

    public void pointToLogcat(){
        logSystemOut = false;
    }

    public void pointToSystemOut(){
        logSystemOut = true;
    }

    private void writeToSystemOut(String level, String tag, String msg) {
        String output = format(level,tag,msg);
        System.out.print(output);
    }

}
