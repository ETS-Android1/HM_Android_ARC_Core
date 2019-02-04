package com.healthymedium.arc.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.Locale;

public class NotificationScheduleJob extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("NotificationScheduleJob","onStartJob");

        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(this);
        }
        if(NotificationManager.getInstance()==null){
            NotificationManager.initialize(this);
        }
        NotificationManager.getInstance().scheduleAllNotifications();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("NotificationNotifyJob","onStopJob");
        return false;
    }







}