package com.healthymedium.arc.notifications;

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
    private Handler handler = new Handler();

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
        locked = false;
    }

    public void start(){
        if(nodes.size()==0){
            listener.onFinished();
            return;
        }
        currentNode = nodes.remove(0);
        long delay = currentNode.time.getMillis() - DateTime.now().getMillis();
        handler.postDelayed(runnable,delay);

        int seconds = (int) (delay/1000);
        int minutes = seconds/60;
        int hours = minutes/60;

        seconds -= minutes*60;
        minutes -= hours*60;

        Log.i(tag,"next notification in "+hours+"hr "+minutes+"min "+seconds+"sec");
    }

    public void stop(){
        handler.removeCallbacks(runnable);
    }

    public boolean isValid(){
        return nodes.size()>0;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            listener.onNotify(currentNode);
            start();
        }
    };

    public interface Listener {
        void onFinished();
        void onNotify(NotificationNode node);
    }

}
