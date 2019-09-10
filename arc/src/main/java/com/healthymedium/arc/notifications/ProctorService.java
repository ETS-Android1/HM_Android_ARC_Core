package com.healthymedium.arc.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.core.MainActivity;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class ProctorService extends Service {

    private static final String tag = "ProctorService";

    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_REFRESH_DATA = "ACTION_REFRESH_DATA";

    private ProctorServiceHandler serviceHandler;
    private Handler handler = new Handler();
    private boolean intentionalDestruction = false;

    public ProctorService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(tag, "onDestroy");
        if(!intentionalDestruction){
            Log.w(tag, "attempting to resuscitate");
            Proctor.startService(this);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        Log.d(tag, action);

        switch (action) {
            case ACTION_START_SERVICE:
                if(serviceHandler!=null){
                    Log.d(tag, "service handler is not null, exiting");
                    break;
                }
                startForegroundService();
                serviceHandler = new ProctorServiceHandler(listener);
                serviceHandler.start();
                ProctorWatchdogJob.start(this);
                break;

            case ACTION_STOP_SERVICE:
                intentionalDestruction = true;
                if(serviceHandler!=null){
                    serviceHandler.stop();
                }
                stopForegroundService();
                ProctorWatchdogJob.stop(this);
                break;

            case ACTION_REFRESH_DATA:
                startForegroundService();
                if(serviceHandler==null) {
                    serviceHandler = new ProctorServiceHandler(listener);
                } else {
                    serviceHandler.stop();
                    serviceHandler.refreshData();
                }
                if(!serviceHandler.isValid()){
                    return START_NOT_STICKY;
                }

                serviceHandler.start();
                break;
        }

        return START_STICKY;
    }

    private void startForegroundService() {
        Log.d(tag, "startForegroundService");

        NotificationUtil.createChannel(this,NotificationTypes.TestProctor);

        PackageManager packageManager = getPackageManager();
        String packageName = getPackageName();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationTypes.TestProctor.getChannelId())
                .setContentTitle(ViewUtil.getString(R.string.notification_testproctor_header))
                .setContentText(ViewUtil.getString(R.string.notification_testproctor_body))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private void stopForegroundService() {
        Log.d(tag, "stopForegroundService");
        stopForeground(true);
        stopSelf();
    }

    ProctorServiceHandler.Listener listener = new ProctorServiceHandler.Listener() {
        @Override
        public void onFinished() {
            Log.d(tag, "onFinished");
            Proctor.stopService(getApplicationContext());
        }

        @Override
        public void onNotify(final NotificationNode node) {
            Log.d(tag, "onNotify");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    //noinspection deprecation
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                            PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP,
                            tag+"_"+SystemClock.uptimeMillis());

                    wakeLock.acquire();
                    NotificationManager.getInstance().notifyUser(node);
                    wakeLock.release();
                }
            });
        }
    };

}
