package com.healthymedium.arc.notifications.type;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.utilities.PreferencesManager;

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
    public String getContent(Context context) {
        return context.getString(R.string.notification_missed);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        boolean showUser = false;

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