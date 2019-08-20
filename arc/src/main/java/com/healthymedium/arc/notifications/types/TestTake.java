package com.healthymedium.arc.notifications.types;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class TestTake extends NotificationType {

    private DateTime expirationTime;
    private String body;

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
    public String getContent(NotificationNode node) {
        TestCycle cycle = Study.getCurrentTestCycle();
        List<TestSession> testSessions = cycle.getTestSessions();
        int sessionId = node.id;
        TestSession session = testSessions.get(sessionId);

        expirationTime = session.getExpirationTime();

        // if first test of the cycle
        if (session.getIndex() == 0 && session.getDayIndex() == 0) {
            body = ViewUtil.getString(R.string.notification1_firstday);
        }

        // if first of day 4
        else if (session.getIndex() == 0 && session.getDayIndex() == 3) {
            body = ViewUtil.getString(R.string.notification1_halfway);
        }

        // if first of day
        else if (session.getIndex() == 0) {
            body = ViewUtil.getString(R.string.notification1_default);
        }

        // if second of day
        else if (session.getIndex() == 1) {
            body = ViewUtil.getString(R.string.notifications2_default);
        }

        // if third of day
        else if (session.getIndex() == 2) {
            body = ViewUtil.getString(R.string.notification3_default);
        }

        // if last of cycle
        else if (session.getIndex() == 3 && session.getDayIndex() == 6) {
            body = ViewUtil.getString(R.string.notification4_lastday);
        }

        // if last of day
        else if (session.getIndex() == 3) {
            body = ViewUtil.getString(R.string.notification4_default);
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm a");
        String time = fmt.print(expirationTime);
        return body.replace("{TIME}", time);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }

}
