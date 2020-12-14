package com.healthymedium.analytics;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    Listener listener;

    public CrashHandler(){

    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Report report = new Report(throwable);
        if(listener!=null){
            listener.onNewReport(report);
        }
        Runtime.getRuntime().exit(0);
    }

    public interface Listener {
        void onNewReport(Report report);
    }

}
