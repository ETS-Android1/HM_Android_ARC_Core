package com.healthymedium.arc.study;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.healthymedium.analytics.Log;

public class BootReceiver extends BroadcastReceiver {

    static private final String tag = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag,"onReceive");

        String action = intent.getAction();
        boolean isReboot = action.equals(Intent.ACTION_BOOT_COMPLETED);

        // if intent is not from a reboot, nope out
        if (!isReboot){
            return;
        }

        Participant participant = Study.getParticipant();
        StateMachine stateMachine = Study.getStateMachine();

        if(participant.isCurrentlyInTestSession() && participant.checkForTestAbandonment()){
            Log.i(tag, participant.getCurrentTestSession().getTestData().toString());
            stateMachine.abandonTest();
        }

    }
}