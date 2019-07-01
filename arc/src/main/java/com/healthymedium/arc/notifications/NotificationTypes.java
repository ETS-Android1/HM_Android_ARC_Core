package com.healthymedium.arc.notifications;

import com.healthymedium.arc.notifications.type.TestProctor;
import com.healthymedium.arc.notifications.type.TestConfirmed;
import com.healthymedium.arc.notifications.type.TestMissed;
import com.healthymedium.arc.notifications.type.TestNext;
import com.healthymedium.arc.notifications.type.TestTake;
import com.healthymedium.arc.notifications.type.VisitNextDay;
import com.healthymedium.arc.notifications.type.VisitNextMonth;
import com.healthymedium.arc.notifications.type.VisitNextWeek;

public class NotificationTypes {

    public static TestProctor TestProctor = new TestProctor();
    public static TestConfirmed TestConfirmed = new TestConfirmed();
    public static TestMissed TestMissed = new TestMissed();
    public static TestNext TestNext = new TestNext();
    public static TestTake TestTake = new TestTake();
    public static VisitNextDay VisitNextDay = new VisitNextDay();
    public static VisitNextWeek VisitNextWeek = new VisitNextWeek();
    public static VisitNextMonth VisitNextMonth = new VisitNextMonth();

}
