package com.healthymedium.arc.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.MainActivity;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Visit;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationManager {

    static public final String NOTIFICATION_ID = "notification_id";
    static public final String NOTIFICATION_TYPE = "notification_type";
    static private final String NOTIFICATION_NODES = "notification_nodes";
    static private final String NOTIFICATION_REQUEST_INDEX = "notification_request_index";
    static private final String NOTIFICATION_CHANNELS_CREATED = "notification_channels_created";

    public static final int TEST_TAKE = 1;
    static public final String CHANNEL_TEST_TAKE_ID = "TEST_TAKE";
    static public final String CHANNEL_TEST_TAKE_NAME = "Test Reminder";
    static public final String CHANNEL_TEST_TAKE_DESC = "Notifies user when it is time to take a test";

    public static final int TEST_MISSED = 2;
    static public final String CHANNEL_TEST_MISSED_ID = "TEST_MISSED";
    static public final String CHANNEL_TEST_MISSED_NAME = "Test Missed";
    static public final String CHANNEL_TEST_MISSED_DESC = "Notifies user when a test was missed";

    public static final int TEST_CONFIRM = 3;
    static public final String CHANNEL_TEST_CONFIRM_ID = "TEST_CONFIRM";
    static public final String CHANNEL_TEST_CONFIRM_NAME = "Test Confirmation";
    static public final String CHANNEL_TEST_CONFIRM_DESC = "Notifies user when a test date confirmation is needed";

    public static final int TEST_NEXT = 4;
    static public final String CHANNEL_TEST_NEXT_ID = "TEST_NEXT";
    static public final String CHANNEL_TEST_NEXT_NAME = "Next Test Date";
    static public final String CHANNEL_TEST_NEXT_DESC = "Notifies user of the next test date";

    public static final int VISIT_NEXT_MONTH = 5;
    static public final String CHANNEL_VISIT_NEXT_MONTH_ID = "VISIT_NEXT_MONTH";
    static public final String CHANNEL_VISIT_NEXT_MONTH_NAME = "Next Testing Cycle Date";
    static public final String CHANNEL_VISIT_NEXT_MONTH_DESC = "Notifies the user 1 month before their next testing cycle";

    public static final int VISIT_NEXT_WEEK = 6;
    static public final String CHANNEL_VISIT_NEXT_WEEK_ID = "VISIT_NEXT_WEEK";
    static public final String CHANNEL_VISIT_NEXT_WEEK_NAME = "Next Testing Cycle Date";
    static public final String CHANNEL_VISIT_NEXT_WEEK_DESC = "Notifies the user 1 week before their next testing cycle";

    public static final int VISIT_NEXT_DAY = 7;
    static public final String CHANNEL_VISIT_NEXT_DAY_ID = "VISIT_NEXT_DAY";
    static public final String CHANNEL_VISIT_NEXT_DAY_NAME = "Next Testing Cycle Date";
    static public final String CHANNEL_VISIT_NEXT_DAY_DESC = "Notifies the user 1 day before their next testing cycle";

    private List<Node> nodes;
    private int requestIndex;
    private Context context;

    private static NotificationManager instance;

    private NotificationManager(Context context) {
        this.context = context;
        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(context);
        }

        if(!PreferencesManager.getInstance().contains(NOTIFICATION_CHANNELS_CREATED)){
            createNotificationChannel(context,CHANNEL_TEST_TAKE_ID,CHANNEL_TEST_TAKE_NAME,CHANNEL_TEST_TAKE_DESC);
            createNotificationChannel(context,CHANNEL_TEST_MISSED_ID,CHANNEL_TEST_MISSED_NAME,CHANNEL_TEST_MISSED_DESC);
            createNotificationChannel(context,CHANNEL_TEST_CONFIRM_ID,CHANNEL_TEST_CONFIRM_NAME,CHANNEL_TEST_CONFIRM_DESC);
            createNotificationChannel(context,CHANNEL_TEST_NEXT_ID,CHANNEL_TEST_NEXT_NAME,CHANNEL_TEST_NEXT_DESC);
            createNotificationChannel(context,CHANNEL_VISIT_NEXT_MONTH_ID,CHANNEL_VISIT_NEXT_MONTH_NAME,CHANNEL_VISIT_NEXT_MONTH_DESC);
            createNotificationChannel(context,CHANNEL_VISIT_NEXT_WEEK_ID,CHANNEL_VISIT_NEXT_WEEK_NAME,CHANNEL_VISIT_NEXT_WEEK_DESC);
            createNotificationChannel(context,CHANNEL_VISIT_NEXT_DAY_ID,CHANNEL_VISIT_NEXT_DAY_NAME,CHANNEL_VISIT_NEXT_DAY_DESC);
            PreferencesManager.getInstance().putBoolean(NOTIFICATION_CHANNELS_CREATED,true);
        }

        if(PreferencesManager.getInstance().contains(NOTIFICATION_NODES)) {
            Node[] nodeArrays = PreferencesManager.getInstance().getObject(NOTIFICATION_NODES,Node[].class);
            nodes = Collections.synchronizedList(new ArrayList<>(Arrays.asList(nodeArrays)));
        } else {
            nodes = Collections.synchronizedList(new ArrayList<NotificationManager.Node>());
        }
        requestIndex = PreferencesManager.getInstance().getInt(NOTIFICATION_REQUEST_INDEX,0);

    }

    public static synchronized void initialize(Context context) {
        instance = new NotificationManager(context);
    }

    public static synchronized NotificationManager getInstance() {
        return instance;
    }

    private void saveNodes(){
        PreferencesManager.getInstance().putObject(NOTIFICATION_NODES,nodes.toArray());
        PreferencesManager.getInstance().putInt(NOTIFICATION_REQUEST_INDEX,requestIndex);

    }

    public Notification buildNotification(Node content, String channel) {
        Log.i("NotificationManager","buildNotification(channel=\""+channel+"\")");

        Config.OPENED_FROM_NOTIFICATION = true; // in case the app is already running

        if (channel.equals("VISIT_NEXT_DAY") || channel.equals("VISIT_NEXT_MONTH") || channel.equals("VISIT_NEXT_WEEK")) {
            Config.OPENED_FROM_VISIT_NOTIFICATION = true;
        }

        Intent main = new Intent(context, MainActivity.class);

        if (channel.equals("VISIT_NEXT_DAY") || channel.equals("VISIT_NEXT_MONTH") || channel.equals("VISIT_NEXT_WEEK")) {
            main.putExtra("OPENED_FROM_VISIT_NOTIFICATION", true);
        } else {
            main.putExtra("OPENED_FROM_NOTIFICATION",true);
        }

        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,main, 0);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.pluck);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channel)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(getContent(content.type))
                .setVibrate(new long[]{500,250,125,250})
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context,R.color.primary))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getContent(content.type)))
                .setSound(sound)
                .setSmallIcon(R.drawable.notification);
        return builder.build();
    }

    public Notification buildNotification(String content, String channel) {
        Log.i("NotificationManager","buildNotification");

        Config.OPENED_FROM_NOTIFICATION = true; // in case the app is already running

        Intent main = new Intent(context, MainActivity.class);
        main.putExtra("OPENED_FROM_NOTIFICATION", true);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,main, 0);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.pluck);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channel)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(content)
                .setVibrate(new long[]{500,250,125,250})
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context,R.color.primary))
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setSmallIcon(R.drawable.notification);
        return builder.build();
    }

    public Notification buildDataNotification(String content, String channel) {
        Log.i("NotificationManager","buildDataNotification(channel=\""+channel+"\")");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channel)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(content)
                .setColor(ContextCompat.getColor(context,R.color.primary))
                .setSmallIcon(R.drawable.notification)
                .setProgress(0, 0, true);
        return builder.build();
    }

    public void scheduleAllNotifications(){
        Log.i("NotificationManager", "scheduleAllNotifications");

        // We're occasionally seeing exceptions when accessing nodes by index (nodes.get()).
        // So, let's try stepping stepping through the list, checking to make sure we haven't
        // reached the end on each iteration.
        
        int i = 0;

        while(i < nodes.size())
        {
            try {
                Node node = nodes.get(i);
                DateTime time = new DateTime(node.time);
                if(time.isAfterNow()) {
                    scheduleNotification(node.id,node.type,time);
                    i++;
                } else {
                    nodes.remove(i);
                }
            }
            catch (java.lang.IndexOutOfBoundsException e)
            {
                Log.i("NotificationManager", e.toString());
                break;
            }

        }
        saveNodes();
    }

    public void scheduleNotification(int sessionId, int type, DateTime timeStamp) {
        Log.i("NotificationManager","scheduleNotification(type="+type+" ,id="+sessionId+")");
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);

        Node node = getNode(type,sessionId);
        if(node==null){
            requestIndex++;
            node = new Node(sessionId, type, requestIndex, timeStamp);
            addNode(node);
        }

        notificationIntent.putExtra(NOTIFICATION_ID, node.id);
        notificationIntent.putExtra(NOTIFICATION_TYPE, node.type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, node.requestCode, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeStamp.getMillis(), pendingIntent);
    }

    public boolean removeNotification(int sessionId,int type) {
        Log.i("NotificationManager","removeNotification(id="+sessionId+", type="+type+")");
        Node node = getNode(type, sessionId);
        if(node==null){
            return false;
        }

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);

        notificationIntent.putExtra(NOTIFICATION_ID, node.id);
        notificationIntent.putExtra(NOTIFICATION_TYPE, node.type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, node.requestCode, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        removeNode(node);
        return true;
    }

    private String getContent(int type){
        String content = new String();
        switch (type){
            case TEST_TAKE:
                // It’s wakeTime to take a quick session!
                content = context.getString(R.string.notification_take);
                break;
            case TEST_CONFIRM:
                // Please confirm your next session date.
                content = context.getString(R.string.notification_confirm);
                break;
            case TEST_MISSED:
                // You’ve missed your tests. If you're unable to finish this week, please contact your site coordinator.
                content = context.getString(R.string.notification_missed);
                break;
            case TEST_NEXT:
                // Your next session will be on 12/31/18
                content = context.getString(R.string.notification_next).replace("{DATE}",Study.getInstance().getParticipant().getCurrentVisit().getActualStartDate().toString(context.getString(R.string.format_date)));
                break;
            case VISIT_NEXT_MONTH:
                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d");
                DateTime startDate = Study.getInstance().getParticipant().getCurrentVisit().getActualStartDate();
                String start = fmt.print(startDate);
                content = context.getString(R.string.notification_1month).replace("{DATE}", start);
                break;
            case VISIT_NEXT_WEEK:
                content = context.getString(R.string.notification_1week);
                break;
            case VISIT_NEXT_DAY:
                content = context.getString(R.string.notification_1day);
                break;
        }
        return content;
    }

    public Node getNode(int type, int sessionId){
        int size = nodes.size();
        for(int i=0;i<size;i++){
            if(nodes.get(i).id==sessionId && nodes.get(i).type==type){
                return nodes.get(i);
            }
        }
        return null;
    }

    public boolean removeNode(Node node){
        boolean result = nodes.remove(node);
        saveNodes();
        return result;
    }

    public void addNode(Node node){
        nodes.add(node);
        saveNodes();
    }

    public class Node{
        public Integer id;
        public Integer type;
        public DateTime time;
        public Integer requestCode;

        Node(Integer id, Integer type, Integer requestCode, DateTime time){
            this.id = id;
            this.type = type;
            this.time = time;
            this.requestCode = requestCode;
        }
    }

    public void notifyUser(int id,int type){
        Log.i("NotificationManager","notifyUser(id="+id+", type="+type+")");

        Node node = getNode(type,id);
        if(node != null) {
            removeNode(node);

            String channel = "";
            switch (type){
                case TEST_TAKE:
                    channel = CHANNEL_TEST_TAKE_ID;
                    break;
                case TEST_MISSED:
                    channel = CHANNEL_TEST_MISSED_ID;
                    break;
                case TEST_CONFIRM:
                    channel = CHANNEL_TEST_CONFIRM_ID;
                    break;
                case TEST_NEXT:
                    channel = CHANNEL_TEST_NEXT_ID;
                    break;
                case VISIT_NEXT_MONTH:
                    channel = CHANNEL_VISIT_NEXT_MONTH_ID;
                    break;
                case VISIT_NEXT_WEEK:
                    channel = CHANNEL_VISIT_NEXT_WEEK_ID;
                    break;
                case VISIT_NEXT_DAY:
                    channel = CHANNEL_VISIT_NEXT_DAY_ID;
                    break;
            }

            int notificationId = makeNotificationId(id, type);

            Notification notification = buildNotification(node,channel);
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification);
        }
    }

    public static void createNotificationChannel(Context context, String id, String name, String description) {
        Log.i("NotificationManager","createNotificationChannel(id="+id+" ,name=\""+name+"\" ,description=\""+description+"\")");
        // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, android.app.NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);

            // Register the channel with the system
            // We can't change the importance or other notification behaviors after this
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // We need to make sure that notification id's are unique, but we also need to be able to
    // recreate a notification id for a given session.
    // This should separate notification ids for the different notification types into their own
    // sections, so as long as we're not going over 10,000 sessions, we shouldn't run into any
    // collisions.

    public int makeNotificationId(int sessionId, int type)
    {
        return (type * 10000) + sessionId;
    }
}