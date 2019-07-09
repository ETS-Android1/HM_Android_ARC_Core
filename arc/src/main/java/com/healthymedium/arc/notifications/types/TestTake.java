package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;

public class TestTake extends NotificationType {

    public TestTake(){
        super();
        id = 1;
        channelId = "TEST_TAKE";
        channelName = "Test Reminder";
        channelDesc = "Notifies user when it is time to take a test";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(Context context) {
        return context.getString(R.string.notification_take);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
