package com.healthymedium.arc.notifications;

import com.healthymedium.arc.notifications.types.TestProctor;
import com.healthymedium.arc.notifications.types.TestConfirmed;
import com.healthymedium.arc.notifications.types.TestMissed;
import com.healthymedium.arc.notifications.types.TestNext;
import com.healthymedium.arc.notifications.types.TestTake;
import com.healthymedium.arc.notifications.types.VisitNextDay;
import com.healthymedium.arc.notifications.types.VisitNextMonth;
import com.healthymedium.arc.notifications.types.VisitNextWeek;

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
