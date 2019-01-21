package com.healthymedium.sample;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;

public class Application extends  com.healthymedium.arc.core.Application{

    @Override
    public void onCreate() {

        // Core
        Config.REST_ENDPOINT = "https://thinkhealthymedium.com/";
        Config.REST_BLACKHOLE = true;

        super.onCreate();
    }

    // register different behaviors here
    public void registerStudyComponents() {
        //Study.getInstance().registerParticipantBehavior();
        //Study.getInstance().registerMigrationBehavior();
        //Study.getInstance().registerSchedulerBehavior();
        //Study.getInstance().registerRestClient();
        Study.getInstance().registerStateMachine(SampleStateMachine.class);
    }

}
