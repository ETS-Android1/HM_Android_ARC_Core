package com.healthymedium.arc.notifications;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Comparator;

public class NotificationNode {
    public int id;
    public int type;
    public DateTime time;
    public int requestCode;

    NotificationNode(int id, int type, int requestCode, DateTime time){
        this.id = id;
        this.type = type;
        this.time = time;
        this.requestCode = requestCode;
    }

    // We need to make sure that notification id's are unique, but we also need to be able to
    // recreate a notification id for a given session.
    // This should separate notification ids for the different notification types into their own
    // sections, so as long as we're not going over 10,000 sessions, we shouldn't run into any
    // collisions.
    public int getNotifyId(){
        return (type * 10000) + id;
    }

    @NonNull
    @Override
    public String toString() {
        return "id="+id+" type="+type+" time="+time.toString();
    }

    public static class TimeComparator implements Comparator<NotificationNode>{
        public int compare(NotificationNode a, NotificationNode b){
            return a.time.compareTo(b.time);
        }
    }

    public static int getNotifyId(int id, int type){
        return (type * 10000) + id;
    }
}
