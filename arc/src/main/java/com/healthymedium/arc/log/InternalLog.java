package com.healthymedium.arc.log;

import com.healthymedium.arc.core.Config;

public class InternalLog extends BaseLog {

    private boolean logSystemOut = false;

    public InternalLog() {
        super("debug.log",true);
        if(Config.ENABLE_LOGGING){
            write("--------------------\n");
        }
    }

    public void v(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            write("V",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("V",tag,msg);
            return;
        }
        android.util.Log.v(tag,msg);
    }

    public void d(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            write("D",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("D",tag,msg);
            return;
        }
        android.util.Log.d(tag,msg);
    }

    public void i(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            write("I",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("I",tag,msg);
            return;
        }
        android.util.Log.i(tag,msg);

    }

    public void w(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            write("W",tag,msg);
        }
        if(logSystemOut){
            writeToSystemOut("W",tag,msg);
            return;
        }
        android.util.Log.w(tag,msg);
    }

    public void e(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
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
