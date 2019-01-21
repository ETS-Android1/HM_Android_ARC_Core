package com.healthymedium.arc.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.healthymedium.arc.utilities.PreferencesManager;

public class NotificationAutoStart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            if(PreferencesManager.getInstance()==null){
                PreferencesManager.initialize(context);
            }
            if(NotificationManager.getInstance()==null){
                NotificationManager.initialize(context);
            }
            NotificationManager.getInstance().scheduleAllNotifications();
        }
    }
}