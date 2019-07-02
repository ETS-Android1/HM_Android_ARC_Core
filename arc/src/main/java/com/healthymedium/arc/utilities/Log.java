package com.healthymedium.arc.utilities;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Log {

    private static FileOutputStream stream;


    public static String format(String level, String tag, String msg) {
        //int time = (int) (System.currentTimeMillis()/1000);
        String time = DateTime.now().toString();

        return time+"/"+level+"/"+tag+": "+msg+"\n";
    }

    private static boolean checkStream(){
        if(stream!=null){
            return true;
        }
        if(Application.getInstance()==null){
           return false;
        }
        File logFile = new File(Application.getInstance().getCacheDir()+"/Log");
        if(!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            stream = new FileOutputStream(logFile,true);
            String divider = "--------------------\n";
            stream.write(divider.getBytes());
            stream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void writeToFile(String level, String tag, String msg) {
        String output = format(level,tag,msg);
        if(checkStream()){
            try {
                stream.write(output.getBytes());
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int v(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("V",tag,msg);
        }
        return android.util.Log.v(tag,msg);
    }

    public static int d(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("D",tag,msg);
        }
        return android.util.Log.d(tag,msg);
    }

    public static int i(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("I",tag,msg);
        }
        return android.util.Log.d(tag,msg);

    }

    public static int w(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("W",tag,msg);
        }
        return android.util.Log.d(tag,msg);
    }

    public static int e(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("E",tag,msg);
        }
        return android.util.Log.d(tag,msg);
    }

    public static int wtf(String tag, String msg) {
        if(Config.ENABLE_LOGGING){
            writeToFile("WTF",tag,msg);
        }
        return android.util.Log.wtf(tag,msg);
    }

    // add other methods if required...
}