package com.healthymedium.arc.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.BuildConfig;
import android.util.Log;

import com.healthymedium.arc.notifications.Proctor;
import com.healthymedium.arc.study.Study;

//  This receiver catches when the user modifies their date/time. When this happens, the app can occasionally
//  get left in an odd state, so we basically just make sure that any existing tests are abandoned,
//  and have the Study State Machine re-run its path deciding methods.

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TimeChangeReceiver", "onReceive("+intent.getAction()+")");

        if(Study.getStateMachine().isCurrentlyInTestPath()) {
            return;
        }

        Proctor.pauseService(context);
        Study.getStateMachine().decidePath();
        Study.getStateMachine().save(true);
        Study.getParticipant().save();
        Proctor.resumeService(context);
    }

}
