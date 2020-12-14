package com.healthymedium.analytics.models;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.healthymedium.analytics.R;

public class AppInfo {

    String launcherName;
    String packageName;
    long versionCode;
    String versionName;
    boolean inForeground;

    public AppInfo(Context context){
        launcherName = context.getResources().getString(R.string.app_name);
        packageName = context.getPackageName();

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setVisible(boolean visible){
        this.inForeground = visible;
    }

}
