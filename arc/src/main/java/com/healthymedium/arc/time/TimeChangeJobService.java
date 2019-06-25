package com.healthymedium.arc.time;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.study.Study;

public class TimeChangeJobService extends JobService {

    public static final int ID = 9900;
    static final String tag = "TimeChangeJobService";
    JobParameters params;
    boolean uploading = false;
    boolean done = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.params = jobParameters;
        Log.i(tag,"onStartJob");

        if(NotificationManager.getInstance()==null){
            NotificationManager.initialize(getApplicationContext());
        }

        // if cache is present, set callback in rest client to end job afterward
        RestClient client = Study.getRestClient();
        client.setUploadListener(new RestClient.UploadListener() {
            @Override
            public void onStart() {
                uploading = true;
            }

            @Override
            public void onStop() {
                Study.getRestClient().removeUploadListener();
                uploading = false;

                if(done) {
                    Log.i(tag,"jobFinished");
                    jobFinished(params, false);
                }
            }
        });

        Log.i(tag, "deciding path");
        Study.getStateMachine().decidePath();

        Log.i(tag, "saving");
        Study.getStateMachine().save(true);
        Study.getParticipant().save();

        done = true;

        if(uploading){
            Log.i(tag,"upload in progress, wait to kill");
            return false;
        } else {
            Log.i(tag,"jobFinished");
            return true;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(tag,"onStopJob");
        return false;
    }

}