package com.healthymedium.arc.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.StudyStateMachine;


/*
This receiver catches when the user modifies their date/time. When this happens, the app can occasionally
get left in an odd state, so we basically just make sure that any existing tests are abandoned,
and have the Study State Machine re-run its path deciding methods.

 */

public class TimeChangeReceiver extends BroadcastReceiver {

    private static TimeChangeReceiver instance;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TimeChangeReceiver", "Received Intent!");
        Log.i("TimeChangeReceiver", intent.toString());

        if(Study.isValid() == false)
        {
            Study.initialize(context);
            NotificationManager.initialize(context);
            Application.getInstance().registerStudyComponents();
            Study.getInstance().load();
        }

        Participant participant = Study.getParticipant();
        StudyStateMachine stateMachine = Study.getStateMachine();
        if(participant.isCurrentlyInTestSession() && participant.checkForTestAbandonment())
        {
            stateMachine.abandonTest();
        }
        Study.getInstance().run();
    }


    public static void registerSelf(Context context)
    {
        Log.i("TimeChangeReceiver", "Registering Self");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);

        instance = new TimeChangeReceiver();

        context.registerReceiver(instance, filter);
    }
}
