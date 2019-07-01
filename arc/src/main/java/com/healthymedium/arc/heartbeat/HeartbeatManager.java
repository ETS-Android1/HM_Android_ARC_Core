package com.healthymedium.arc.heartbeat;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

public class HeartbeatManager {

    private static final String tag = "HeartbeatManager";
    private static final String TAG_LAST_HEARTBEAT = "TAG_LAST_HEARTBEAT";
    private static final int jobId = 9000;

    private static HeartbeatManager instance;
    private Listener listener;
    private Context context;

    private long lastHeartbeat;

    private HeartbeatManager(Context context) {
        this.context = context;
        
        lastHeartbeat = PreferencesManager.getInstance().getLong(TAG_LAST_HEARTBEAT,0);
        Log.i(tag, "last heartbeat = "+lastHeartbeat);
    }

    public static synchronized HeartbeatManager getInstance() {
        if(instance==null){
            instance = new HeartbeatManager(Application.getInstance());
        }
        return instance;
    }

    public void tryHeartbeat(RestClient restClient, String participantId, final Listener listener){
        this.listener = listener;

        if(Config.REST_BLACKHOLE || !Config.REST_HEARTBEAT){
            if(listener!=null){
                listener.onSuccess(false);
            }
            return;
        }

        Log.i(tag,"try heartbeat");
        DateTime time = new DateTime(lastHeartbeat*1000L).plusDays(1);
        if(time.isBeforeNow()) {
            restClient.sendHeartbeat(participantId, restListener);
        } else if(listener!=null){
            Log.i(tag,"too soon, skipping api call");
            listener.onSuccess(false);
        }

    }

    public void scheduleHeartbeat(){
        Log.i(tag,"schedule heartbeat job");
        ComponentName serviceComponent = new ComponentName(context, HeartbeatJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(6*60*60*1000); // six hours
        //builder.setPeriodic(15*60*1000); // fifteen minutes
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        builder.setPersisted(true); // set persistant across reboots

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public void unscheduleHeartbeat(){
        Log.i(tag,"unschedule heartbeat job");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }

    public interface Listener{
        void onSuccess(boolean tried);
        void onFailure();
    }

    RestClient.Listener restListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            lastHeartbeat = (DateTime.now().getMillis() / 1000L);
            PreferencesManager.getInstance().putLong(TAG_LAST_HEARTBEAT,lastHeartbeat);
            Log.i(tag, "new heartbeat = "+lastHeartbeat);

            if(listener!=null){
                listener.onSuccess(true);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            if(listener!=null){
                listener.onFailure();
            }
        }
    };

}