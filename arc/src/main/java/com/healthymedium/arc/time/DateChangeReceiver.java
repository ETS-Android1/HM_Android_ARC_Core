package com.healthymedium.arc.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.healthymedium.arc.utilities.PreferencesManager;

public class DateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DATE RECEIVER", "OK");
        PreferencesManager.getInstance().putInt("test_missed_count", 0);
    }
}
