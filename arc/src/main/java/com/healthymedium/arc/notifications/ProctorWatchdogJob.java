package com.healthymedium.arc.notifications;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.study.Study;

public class ProctorWatchdogJob extends JobService {

    private static final String tag = "ProctorWatchdogJob";
    private static final int jobId = 237;

    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.params = jobParameters;
        Log.i(tag, "onStartJob");

        TestCycle cycle = Study.getCurrentTestCycle();
        if (cycle == null) {
            stop(getApplicationContext());
            return false;
        }
        if (cycle.getActualStartDate().isAfterNow() || cycle.getActualEndDate().isBeforeNow()) {
            stop(getApplicationContext());
            return false;
        }

        Proctor.startService(getApplicationContext());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(tag, "onStopJob");
        return false;
    }

    public static void start(Context context) {
        Log.i(tag,"start");

        long fifteenMinutes = 15*60*1000;
        ComponentName serviceComponent = new ComponentName(context, ProctorWatchdogJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(fifteenMinutes);    // fifteen minutes
        builder.setRequiresDeviceIdle(false);   // device should be idle
        builder.setRequiresCharging(false);     // we don't care if the device is charging or not
        builder.setPersisted(true);             // set persistant across reboots

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void stop(Context context) {
        Log.i(tag,"stop");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }

}