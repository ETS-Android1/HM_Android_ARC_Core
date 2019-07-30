package com.healthymedium.arc.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;

public class NotificationRebootReceiver extends BroadcastReceiver {

    static private final String tag = "NotificationRebootRecvr";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag,"onReceive");
        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            return;
        }
        NotificationManager.getInstance().scheduleAllNotifications();
        TestCycle cycle = Study.getCurrentTestCycle();
        if(cycle ==null){
            return;
        }
        if(cycle.getActualStartDate().isBeforeNow() && cycle.getActualEndDate().isAfterNow()){
            Log.i(tag,"starting proctor service");
            Proctor.startService(Application.getInstance());
        }
    }
}