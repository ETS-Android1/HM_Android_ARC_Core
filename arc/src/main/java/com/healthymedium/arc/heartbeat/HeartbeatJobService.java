package com.healthymedium.arc.heartbeat;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.StateMachine;

import static com.healthymedium.arc.study.Study.getRestClient;

public class HeartbeatJobService extends JobService {

    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.params = jobParameters;
        Log.i("HeartbeatJobService","onStartJob");

        Log.i("HeartbeatJobService","initializing manager");
        if(HeartbeatManager.getInstance()==null){
            HeartbeatManager.initialize(this);
        }

        if(!Study.getStateMachine().isIdle()){
            return false;
        }

        HeartbeatManager.getInstance().startNotification();

        if(!Config.REST_HEARTBEAT){
            checkUploadQueue();
            return true;
        }

        Log.i("HeartbeatJobService","trying heartbeat");
        String participantId = Study.getParticipant().getId();
        HeartbeatManager.getInstance().tryHeartbeat(getRestClient(), participantId, new HeartbeatManager.Listener() {
            @Override
            public void onSuccess(boolean tried) {
                checkUploadQueue();
            }

            @Override
            public void onFailure() {
                HeartbeatManager.getInstance().stopNotification();
                jobFinished(params,false);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("HeartbeatJobService","onStopJob");
        return false;
    }

    private void checkUploadQueue(){
        Log.i("HeartbeatJobService","checkUploadQueue");
        RestClient client = Study.getRestClient();
        if(client.isUploadQueueEmpty()){
            checkState();
            HeartbeatManager.getInstance().stopNotification();
            jobFinished(params,false);
        } else {
            client.setUploadListener(new RestClient.UploadListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {
                    Study.getRestClient().removeUploadListener();
                    checkState();
                    HeartbeatManager.getInstance().stopNotification();
                    jobFinished(params,false);
                }
            });
            client.popQueue();
        }
    }

    private void checkState(){
        Log.i("HeartbeatJobService","checkState");
        StateMachine stateMachine = Study.getStateMachine();
        stateMachine.decidePath();
        stateMachine.save(true);
    }

}