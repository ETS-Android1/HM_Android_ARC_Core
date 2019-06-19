package com.healthymedium.arc.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.healthymedium.arc.core.Locale;
import com.healthymedium.arc.utilities.PreferencesManager;


import static com.healthymedium.arc.study.StateMachine.TAG_TEST_MISSED_COUNT;

public class NotificationNotifyJob extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("NotificationNotifyJob","onStartJob");

        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(this);
        }

        if(PreferencesManager.getInstance().contains(Locale.TAG_LANGUAGE)) {
            String language = PreferencesManager.getInstance().getString(Locale.TAG_LANGUAGE, Locale.LANGUAGE_ENGLISH);
            String country = PreferencesManager.getInstance().getString(Locale.TAG_COUNTRY, Locale.COUNTRY_UNITED_STATES);

            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            conf.setLocale(new java.util.Locale(language, country));
            res.updateConfiguration(conf, res.getDisplayMetrics());
        }

        if(NotificationManager.getInstance()==null){
            NotificationManager.initialize(this);
        }
        int id = params.getExtras().getInt(NotificationManager.NOTIFICATION_ID,0);
        int type = params.getExtras().getInt(NotificationManager.NOTIFICATION_TYPE,1);

        if(type==NotificationManager.TEST_MISSED) {
            int count = PreferencesManager.getInstance().getInt(TAG_TEST_MISSED_COUNT, 0);
            count++;
            if (count == 4) {
                NotificationManager.getInstance().notifyUser(id, type);
                count = 0;
            }
            NotificationManager.getInstance().removeNotification(id,type);
            PreferencesManager.getInstance().putInt(TAG_TEST_MISSED_COUNT, count);
        } else {
            NotificationManager.getInstance().notifyUser(id, type);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("NotificationNotifyJob","onStopJob");
        return false;
    }







}