package com.healthymedium.arc.study;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import com.healthymedium.analytics.Log;

public class AbandonmentJobService extends JobService {

    static private final String tag = "AbandonmentJobService";
    public static final int jobId = 8800;

    @Override
    public boolean onStartJob(JobParameters jobParameters){
        Log.i(tag,"Running abandonment check");

        // First, check that we actually have a test running, and that we've hit our timeout for abandonment
        // Then, mark it abandoned, and send the data

        Participant participant = Study.getParticipant();
        StateMachine stateMachine = Study.getStateMachine();

        if(participant.isCurrentlyInTestSession() && participant.checkForTestAbandonment()){
            Log.i(tag, participant.getCurrentTestSession().getTestData().toString());
            stateMachine.abandonTest();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters){
        return false;
    }

    public static void scheduleSelf(Context context){
        Log.i(tag,"Schedule abandonment job");

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);

        ComponentName serviceComponent = new ComponentName(context, AbandonmentJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);       // device should be idle
        builder.setRequiresCharging(false);         // we don't care if the device is charging or not
        builder.setPersisted(false);                // set persistent across reboots
        builder.setMinimumLatency(300000);          // minimum of 5 minutes before this service gets called
        builder.setOverrideDeadline(600000);        // maximum of 10 minutes before this service gets called
        builder.setBackoffCriteria(300000, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        jobScheduler.schedule(builder.build());
    }

    public static void unscheduleSelf(Context context){
        Log.i(tag,"Cancelling abandonment job");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }
}
