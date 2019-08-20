package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class TestNext extends NotificationType {

    public TestNext(){
        super();
        id = 4;
        channelId = "TEST_NEXT";
        channelName = "Next Test Date";
        channelDesc = "Notifies user of the next test date";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {
        DateTime date = Study.getInstance().getParticipant().getCurrentTestCycle().getActualStartDate();
        String fmtDate = date.toString(ViewUtil.getString(R.string.format_date));
        return ViewUtil.getString(R.string.notification_next).replace("{DATE}", fmtDate);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
