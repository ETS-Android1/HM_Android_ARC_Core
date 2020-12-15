package com.healthymedium.arc.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.healthymedium.arc.notifications.types.NotificationImportance;
import android.util.Log;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.types.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class NotificationUtil {

    static private final String tag = "NotificationUtil";

    public static boolean areNotificationsEnabled(Context context){
        Log.i(tag,"areNotificationsEnabled()");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if(!notificationManager.areNotificationsEnabled()){
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        for(NotificationChannel channel : getChannels(context)){
            if(channel.getImportance() == NotificationImportance.NONE){
                return false;
            }
        }
        return true;
    }

    public static void openNotificationSettings(Context context){
        Log.i(tag,"openNotificationSettings()");
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        context.startActivity(intent);
    }

    // channels ------------------------------------------------------------------------------------

    public static void createChannel(Context context, NotificationType type){
        Log.i(tag,"createNotificationChannel(id="+type.getChannelId()+" ,name=\""+type.getChannelName()+"\" ,description=\""+type.getChannelDesc()+"\")");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(type.getChannelId(), type.getChannelName(), type.getImportance());
        channel.setDescription(type.getChannelDesc());
        channel.setVibrationPattern(new long[]{500,250,125,250});
        channel.setShowBadge(type.shouldShowBadge());
        channel.enableVibration(true);
        channel.enableLights(true);

        if(type.getSoundResource()!=-1){
            Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + type.getSoundResource());
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(sound,attributes);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public static void removeChannel(Context context, NotificationType type){
        Log.i(tag,"removeNotificationChannel(id="+type.getChannelId()+" ,name=\""+type.getChannelName()+"\" ,description=\""+type.getChannelDesc()+"\")");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        android.app.NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(type.getChannelId());
    }

    public static List<NotificationChannel> getChannels(Context context){
        Log.i(tag,"getNotificationChannels()");

        List<NotificationChannel> channels = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            channels = notificationManager.getNotificationChannels();
        }

        return channels;
    }

    public static void removeUnusedChannels(Context context, List<NotificationType> types){
        Log.i(tag,"removeUnusedChannels()");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        List<NotificationChannel> channels = notificationManager.getNotificationChannels();
        for(NotificationChannel channel : channels){

            boolean found = false;
            String id = channel.getId();
            for(NotificationType type : types){
                if(type.getChannelId().equals(id)){
                    found = true;
                    break;
                }
            }
            if(!found){
                notificationManager.deleteNotificationChannel(id);
            }

        }

    }

    // ---------------------------------------------------------------------------------------------

    public static Notification buildNotification(Context context, NotificationNode node, NotificationType type) {
        Log.i(tag,"buildNotification(channel=\""+type.getChannelName()+"\")");

        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        Intent main = packageManager.getLaunchIntentForPackage(packageName);

        if(type.hasExtra()){
            main.putExtra(type.getExtra(), true);
        }

        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,node.requestCode,main, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,type.getChannelId())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(type.getContent(node))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setVibrate(new long[]{500,250,125,250})
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context,R.color.primary))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification);

        int soundResource = type.getSoundResource();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && soundResource != -1) {
            Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + type.getSoundResource());
            builder.setSound(sound);
        }
        return builder.build();
    }

}
