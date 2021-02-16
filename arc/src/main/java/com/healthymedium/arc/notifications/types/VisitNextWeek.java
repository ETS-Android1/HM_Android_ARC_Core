package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.utilities.ViewUtil;

public class VisitNextWeek extends NotificationType {

    public VisitNextWeek(){
        super();
        id = 6;
        channelId = "VISIT_NEXT_WEEK";
        channelName = "Next Testing Cycle, Week Prior";
        channelDesc = "Notifies the user one week before their next testing cycle";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION;
        proctored = false;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {
        return ViewUtil.getString(R.string.notification_weekbefore);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }

    @Override
    public void onNotify(NotificationNode node) {

    }

}
