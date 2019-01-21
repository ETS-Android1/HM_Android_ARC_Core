package com.healthymedium.arc.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.healthymedium.arc.utilities.PreferencesManager;

import java.util.Locale;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("NotificationJobService","onStartJob");

        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(this);
        }

        if(PreferencesManager.getInstance().contains("language")) {
            String language = PreferencesManager.getInstance().getString("language", "en");
            String country = PreferencesManager.getInstance().getString("country", "US");
            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(language, country));
            res.updateConfiguration(conf, res.getDisplayMetrics());
        }

        if(NotificationManager.getInstance()==null){
            NotificationManager.initialize(this);
        }
        int id = params.getExtras().getInt(NotificationManager.NOTIFICATION_ID,0);
        int type = params.getExtras().getInt(NotificationManager.NOTIFICATION_TYPE,1);

        if(type==NotificationManager.TEST_MISSED) {
            int count = PreferencesManager.getInstance().getInt("test_missed_count", 0);
            count++;
            if (count == 4) {
                NotificationManager.getInstance().notifyUser(id, type);
                count = 0;
            }
            NotificationManager.getInstance().removeNotification(id,type);
            PreferencesManager.getInstance().putInt("test_missed_count", count);
        } else {
            NotificationManager.getInstance().notifyUser(id, type);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("NotificationJobService","onStopJob");
        return false;
    }







}