package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class TestMissed extends NotificationType {

    public static final String TAG_TEST_MISSED_COUNT = "TestMissedCount";

    public TestMissed(){
        super();
        id = 2;
        channelId = "TEST_MISSED";
        channelName = "Test Missed";
        channelDesc = "Notifies user when a test was missed";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {
        return ViewUtil.getString(R.string.notification_missedtests);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        boolean showUser = false;

        int notifyId = NotificationNode.getNotifyId(node.id,new TestTake().id);
        NotificationManager.getInstance().removeUserNotification(notifyId);

        PreferencesManager preferences = PreferencesManager.getInstance();
        int count = preferences.getInt(TAG_TEST_MISSED_COUNT, 0);
        count++;

        if (count == 4) {
            showUser = true;
            count = 0;
        }

        preferences.putInt(TAG_TEST_MISSED_COUNT, count);

        return showUser;
    }
}