package com.healthymedium.arc.notifications;

import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.healthymedium.arc.notifications.NotificationManager.NOTIFICATION_ID;
import static com.healthymedium.arc.notifications.NotificationManager.NOTIFICATION_TYPE;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra(NOTIFICATION_ID,0);
        int type = intent.getIntExtra(NOTIFICATION_TYPE,1);

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(NotificationNotifyJob.build(context,id,type));
    }
}