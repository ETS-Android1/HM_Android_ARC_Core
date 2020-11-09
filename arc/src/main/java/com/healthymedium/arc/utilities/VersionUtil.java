package com.healthymedium.arc.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.healthymedium.analytics.Log;

public class VersionUtil {

    private static final String tag = "VersionUtil";

    private static long app_code;
    private static String app_name;

    private VersionUtil(){
        app_name = new String();
    }

    public static void initialize(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_name = packageInfo.versionName;
            app_code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.i(tag,"app       | code="+app_code+", name="+app_name);
        Log.i(tag,"core      | code="+getCoreVersionCode()+", name="+getCoreVersionName());
        Log.i(tag,"analytics | code="+getAnalyticsVersionCode()+", name="+getAnalyticsVersionName());

    }

    public static long getAppVersionCode(){
        return app_code;
    }

    public static String getAppVersionName(){
        return app_name;
    }

    public static long getCoreVersionCode(){
        return com.healthymedium.arc.library.BuildConfig.ARC_VERSION_CODE;
    }

    public static String getCoreVersionName(){
        return com.healthymedium.arc.library.BuildConfig.ARC_VERSION_NAME;
    }

    public static long getAnalyticsVersionCode(){
        return com.healthymedium.analytics.BuildConfig.ANALYTICS_VERSION_CODE;
    }

    public static String getAnalyticsVersionName(){
        return com.healthymedium.analytics.BuildConfig.ANALYTICS_VERSION_NAME;
    }


    public static long getVersionCode(int major,int minor, int patch, int build){
        return major * 1000000 + minor * 10000 + patch * 100 + build;
    }

    public static long getVersionCode(int major,int minor, int patch){
        return getVersionCode(major,minor,patch,0);
    }

    public static long getVersionCode(int major,int minor){
        return getVersionCode(major,minor,0,0);
    }

    public static long getVersionCode(int major){
        return getVersionCode(major,0,0,0);
    }

}
