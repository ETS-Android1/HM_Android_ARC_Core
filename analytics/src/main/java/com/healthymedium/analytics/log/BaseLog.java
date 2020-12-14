package com.healthymedium.analytics.log;

import android.content.Context;

import com.healthymedium.analytics.Log;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

abstract class BaseLog {

    private FileOutputStream stream;
    private boolean locked;
    private long size;
    String name;

    public BaseLog(Context context, String name, boolean append) {
        locked = true;
        this.name = name;

        File file = new File(filename());
        // if file does not exist, create it
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        size = file.length();

        try {
            stream = new FileOutputStream(file,append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        locked = false;
    }

    public BaseLog() {

    }

    public String filename(){
        return Log.directory()+"/"+name;
    }

    public void v(String tag, String msg) {
        write("V",tag,msg);
    }

    public void d(String tag, String msg) {
        write("D",tag,msg);
    }

    public void i(String tag, String msg) {
        write("I",tag,msg);
    }

    public void w(String tag, String msg) {
        write("W",tag,msg);
    }

    public void e(String tag, String msg) {
        write("E",tag,msg);
    }

    public void wtf(String tag, String msg) {
        write("WTF",tag,msg);
    }

    public void write(String level, String tag, String msg) {
        write(format(level,tag,msg));
    }

    public void write(String string) {
        if(checkStream() && !isLocked()){
            try {
                byte[] bytes = string.getBytes();
                size += bytes.length;
                stream.write(bytes);
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean checkStream(){
        return stream!=null;
    }

    protected boolean isLocked(){
        return locked;
    }

    public void lock(){
        locked = true;
    }

    public void unlock(){
        locked = false;
    }
    protected static String format(String level, String tag, String msg) {
        String time = DateTime.now().toString();
        return time+"/"+level+"/"+tag+": "+msg+"\n";
    }

}
