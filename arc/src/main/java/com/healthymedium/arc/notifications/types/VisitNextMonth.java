package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.study.Study;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class VisitNextMonth extends NotificationType {

    public VisitNextMonth(){
        super();
        id = 5;
        channelId = "VISIT_NEXT_MONTH";
        channelName = "Next Testing Cycle, Month Prior";
        channelDesc = "Notifies the user one month before their next testing cycle";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION;
        proctored = false;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(Context context) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d");
        DateTime startDate = Study.getInstance().getParticipant().getCurrentTestCycle().getActualStartDate();
        String start = fmt.print(startDate);
        return context.getString(R.string.notification_1month).replace("{DATE}", start);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
