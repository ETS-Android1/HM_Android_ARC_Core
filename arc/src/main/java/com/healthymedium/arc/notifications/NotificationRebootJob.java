package com.healthymedium.arc.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.study.Study;

public class NotificationRebootJob extends JobService {

    static private final String tag = "NotificationRebootJob";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(tag,"onStartJob");

        NotificationManager.getInstance().scheduleAllNotifications();

        TestCycle cycle = Study.getCurrentTestCycle();
        if(cycle ==null){
            return false;
        }
        if(cycle.getActualStartDate().isBeforeNow() && cycle.getActualEndDate().isAfterNow()){
            Log.i(tag,"starting proctor service");
            Proctor.startService(this);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(tag,"onStopJob");
        return false;
    }

}