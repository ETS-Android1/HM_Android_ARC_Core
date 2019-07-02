package com.healthymedium.arc.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Visit;

public class NotificationRebootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationRebootRecvr","onReceive");
        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            return;
        }
        NotificationManager.getInstance().scheduleAllNotifications();
        Visit visit = Study.getCurrentVisit();
        if(visit==null){
            return;
        }
        if(visit.getActualStartDate().isBeforeNow() && visit.getActualEndDate().isAfterNow()){
            Proctor.startService(Application.getInstance());
        }
    }
}