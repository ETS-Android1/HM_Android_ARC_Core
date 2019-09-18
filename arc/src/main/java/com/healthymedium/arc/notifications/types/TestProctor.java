package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.notifications.NotificationNode;

public class TestProctor extends NotificationType {

    public TestProctor(){
        super();
        id = -1;
        channelId = "TEST_PROCTOR_SERVICE";
        channelName = "Test Proctor Service";
        channelDesc = "Handles time critical notifications for testing";
        importance = NotificationImportance.LOW;
        extra = "";
        proctored = false;
    }

    @Override
    public String getContent(NotificationNode node) {
        return "";
    }

    // not used but still here because it extends an abstract class
    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }
}
