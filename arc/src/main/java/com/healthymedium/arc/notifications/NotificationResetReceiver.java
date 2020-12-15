package com.healthymedium.arc.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;

public class NotificationResetReceiver extends BroadcastReceiver {

    static private final String tag = "NotificationResetReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag,"onReceive");

        String action = intent.getAction();
        boolean isUpdate = action.equals(Intent.ACTION_MY_PACKAGE_REPLACED);
        boolean isReboot = action.equals(Intent.ACTION_BOOT_COMPLETED);

        // if intent is not from either a reboot or an update, nope out
        if (!(isReboot||isUpdate)){
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