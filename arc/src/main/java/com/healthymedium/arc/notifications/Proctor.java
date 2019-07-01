package com.healthymedium.arc.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

public class Proctor {

    public static void startService(Context context) {
        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_START_SERVICE);
        ContextCompat.startForegroundService(context, intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_STOP_SERVICE);
        ContextCompat.startForegroundService(context,intent);
    }

    public static void refreshData(Context context) {
        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_REFRESH_DATA);
        ContextCompat.startForegroundService(context,intent);
    }

}
