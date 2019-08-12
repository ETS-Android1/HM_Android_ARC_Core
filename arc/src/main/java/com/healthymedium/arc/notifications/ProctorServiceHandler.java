package com.healthymedium.arc.notifications;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.healthymedium.arc.utilities.Log;

import com.healthymedium.arc.notifications.types.NotificationType;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProctorServiceHandler {

    private static final String tag = "ProctorServiceHandler";

    private List<NotificationNode> nodes = new ArrayList<>();
    private NotificationTimer timer;

    private NotificationNode currentNode;
    private Listener listener;

    private boolean locked = false;

    ProctorServiceHandler(@NonNull Listener listener){
        this.listener = listener;
        if(listener==null){
            throw new UnsupportedOperationException("ProctorServiceHandler.Listener needs to not be null");
        }
        refreshData();
    }

    public void refreshData() {
        Log.i(tag, "refreshData");
        if(locked){
            return;
        }
        locked = true;
        nodes.clear();

        NotificationManager manager = NotificationManager.getInstance();
        NotificationNodes notificationNodes = NotificationManager.getInstance().getNodes();

        List<NotificationNode> allNodes = Collections.synchronizedList(notificationNodes.getAll());
        for(NotificationNode node : allNodes){

            NotificationType type = manager.getNotificationType(node.type);
            if(type==null){
               continue;
            }
            if(!type.isProctored()) {
                continue;
            }

            if(node.time.isAfterNow()){
                nodes.add(node);
            } else {
                listener.onNotify(node);
            }
        }

        //  sort by time
        Collections.sort(nodes,new NotificationNode.TimeComparator());

        for(NotificationNode node : nodes){
            Log.i(tag, node.toString());
        }

        locked = false;
    }

    public void start(){
        Log.i(tag, "start");
        if(nodes.size()==0){
            listener.onFinished();
            return;
        }
        currentNode = nodes.remove(0);

        long time = currentNode.time.getMillis();
        long delay = time - DateTime.now().getMillis();
        timer = new NotificationTimer(delay,time);
        timer.start();

        int seconds = (int) (delay/1000);
        int minutes = seconds/60;
        int hours = minutes/60;

        seconds -= minutes*60;
        minutes -= hours*60;

        Log.i(tag,"next notification in "+hours+"hr "+minutes+"min "+seconds+"sec");
    }

    public void stop(){
        Log.i(tag, "stop");
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
    }

    public boolean isValid(){
        return nodes.size()>0;
    }

    private void onTimeout(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                stop();
                listener.onNotify(currentNode);
                start();
            }
        });
    }

    public interface Listener {
        void onFinished();
        void onNotify(NotificationNode node);
    }


    // there is an extra ten second buffer in this timer
    // just before onFinish would be called, countdown timers become reliant on sleep state
    // and perform the call after a device wakes up
    //
    // to get around this, we add a ten second buffer to the timer and check against system time on each interval
    // when the system time is after the non buffered time, we execute the timeout
    public class NotificationTimer extends CountDownTimer {

        private static final String tag = "NotificationTimer";
        private static final int tenSeconds = 10*1000;
        long time;

        public NotificationTimer(long delay, long time) {
            super(delay+tenSeconds, 500);
            this.time = time;
        }

        @Override
        public void onFinish() {
            Log.i(tag,"onFinish");
            onTimeout();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long now = System.currentTimeMillis();
            if(now>time){
                Log.i(tag,"onTick - timing out");
                onTimeout();
//            } else { // uncomment for logging
//                int seconds = (int) ((millisUntilFinished-tenSeconds)/1000);
//                int minutes = seconds/60;
//                int hours = minutes/60;
//
//                seconds -= minutes*60;
//                minutes -= hours*60;
//                Log.v(tag,"onTick - "+hours+"hr "+minutes+"min "+seconds+"sec left");
            }
        }

    }

}
