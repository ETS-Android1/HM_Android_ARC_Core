package com.healthymedium.arc.notifications.type;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;

public class TestConfirmed extends NotificationType {

    public TestConfirmed(){
        super();
        id = 3;
        channelId = "TEST_CONFIRM";
        channelName = "Test Confirmation";
        channelDesc = "Notifies user when a test date confirmation is needed";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(Context context) {
        return context.getString(R.string.notification_confirm);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
