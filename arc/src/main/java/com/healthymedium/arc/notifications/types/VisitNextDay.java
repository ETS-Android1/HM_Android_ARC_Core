package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;

public class VisitNextDay extends NotificationType {

    public VisitNextDay(){
        super();
        id = 7;
        channelId = "VISIT_NEXT_DAY";
        channelName = "Next Testing Cycle, Day Prior";
        channelDesc = "Notifies the user one day before their next testing cycle";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION;
        proctored = false;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(Context context) {
        return context.getString(R.string.notification_1day);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
