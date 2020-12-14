package com.healthymedium.analytics.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.healthymedium.analytics.Log;

public class LogReceiver extends BroadcastReceiver {

    static private final String tag = "LogReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag,"onReceive");
        Log.system.i("broadcast",intent.getAction());
    }
}