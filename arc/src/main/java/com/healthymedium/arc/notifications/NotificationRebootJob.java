package com.healthymedium.arc.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Visit;

public class NotificationRebootJob extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("NotificationRebootJob","onStartJob");

        NotificationManager.getInstance().scheduleAllNotifications();

        Visit visit = Study.getCurrentVisit();
        if(visit==null){
            return false;
        }
        if(visit.getActualStartDate().isBeforeNow() && visit.getActualEndDate().isAfterNow()){
            Proctor.startService(this);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("NotificationRebootJob","onStopJob");
        return false;
    }

}