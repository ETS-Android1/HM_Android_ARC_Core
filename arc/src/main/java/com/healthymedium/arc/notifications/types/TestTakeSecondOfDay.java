package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TestTakeSecondOfDay extends NotificationType {

    public TestTakeSecondOfDay(){
        super();
        id = 1;
        channelId = "TEST_TAKE_SECOND";
        channelName = "Test Reminder";
        channelDesc = "Notifies user when it is time to take their second test of the day";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm a");
        DateTime expirationTime = Study.getInstance().getParticipant().getCurrentTestSession().getExpirationTime();
        String time = fmt.print(expirationTime);
        return ViewUtil.getString(R.string.notifications2_default).replace("{TIME}", time);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
