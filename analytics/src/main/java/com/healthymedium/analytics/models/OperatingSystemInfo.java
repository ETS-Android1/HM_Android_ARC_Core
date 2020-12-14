package com.healthymedium.analytics.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

public class OperatingSystemInfo {

    private String name = "Android";
    private Integer sdk = Build.VERSION.SDK_INT;
    private String release = Build.VERSION.RELEASE;
    private String securityPatch = Build.VERSION.SECURITY_PATCH;
    private Boolean autoTimeEnabled;
    private Boolean autoTimeZoneEnabled;
    private Boolean alwaysFinishActivities;
    private Boolean developerOptionsEnabled;

    public OperatingSystemInfo(Context context){
        if(context==null){
            return;
        }
        refresh(context);

        ContentResolver resolver = context.getContentResolver();
        resolver.registerContentObserver(Settings.Global.CONTENT_URI, true, new SettingsObserver(context));
    }

    public void refresh(Context context){
        ContentResolver resolver = context.getContentResolver();
        autoTimeEnabled = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME, 0)==1;
        autoTimeZoneEnabled = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME_ZONE, 0)==1;
        alwaysFinishActivities = Settings.Global.getInt(resolver, Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0)==1;
        developerOptionsEnabled = Settings.Global.getInt(resolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0)==1;

        Log.i("OperatingSystemInfo","autoTimeEnabled = "+autoTimeEnabled);
        Log.i("OperatingSystemInfo","autoTimeZoneEnabled = "+autoTimeZoneEnabled);
        Log.i("OperatingSystemInfo","alwaysFinishActivities = "+alwaysFinishActivities);
        Log.i("OperatingSystemInfo","developerOptionsEnabled = "+developerOptionsEnabled);
    }

    class SettingsObserver extends ContentObserver {

        Context context;

        public SettingsObserver(Context context) {
            super(new Handler());
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if(context==null){
                return;
            }
            refresh(context);

        }
    }
}
