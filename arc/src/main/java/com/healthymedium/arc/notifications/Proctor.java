package com.healthymedium.arc.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.healthymedium.arc.utilities.Log;

public class Proctor {

    private static final String tag = "Proctor";

    public static void startService(Context context) {
        Log.i(tag,"startService");
        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_START_SERVICE);
        ContextCompat.startForegroundService(context, intent);
    }

    public static void stopService(Context context) {
        Log.i(tag,"stopService");
        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_STOP_SERVICE);
        ContextCompat.startForegroundService(context,intent);
    }

    public static void refreshData(Context context) {
        Log.i(tag,"refreshData");
        Intent intent = new Intent(context, ProctorService.class);
        intent.setAction(ProctorService.ACTION_REFRESH_DATA);
        ContextCompat.startForegroundService(context,intent);
    }

}
