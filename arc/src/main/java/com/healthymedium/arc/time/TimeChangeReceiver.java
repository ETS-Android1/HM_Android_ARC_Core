package com.healthymedium.arc.time;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationNotifyJob;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.StudyStateMachine;


/*
This receiver catches when the user modifies their date/time. When this happens, the app can occasionally
get left in an odd state, so we basically just make sure that any existing tests are abandoned,
and have the Study State Machine re-run its path deciding methods.
 */

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TimeChangeReceiver", "received intent");
        Log.i("TimeChangeReceiver", intent.toString());

        ComponentName serviceComponent = new ComponentName(context, TimeChangeJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(TimeChangeJobService.ID,serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPersisted(false);
        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

}
