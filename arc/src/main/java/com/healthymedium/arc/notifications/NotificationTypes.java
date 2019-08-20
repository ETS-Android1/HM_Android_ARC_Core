package com.healthymedium.arc.notifications;

import com.healthymedium.arc.notifications.types.TestProctor;
import com.healthymedium.arc.notifications.types.TestConfirmed;
import com.healthymedium.arc.notifications.types.TestMissed;
import com.healthymedium.arc.notifications.types.TestNext;
import com.healthymedium.arc.notifications.types.TestTake;
import com.healthymedium.arc.notifications.types.TestTakeFirstOfDay;
import com.healthymedium.arc.notifications.types.TestTakeSecondOfDay;
import com.healthymedium.arc.notifications.types.TestTakeThirdOfDay;
import com.healthymedium.arc.notifications.types.TestTakeLastOfDay;
import com.healthymedium.arc.notifications.types.TestTakeFirstOfDay4;
import com.healthymedium.arc.notifications.types.TestTakeFirstOfCycle;
import com.healthymedium.arc.notifications.types.TestTakeLastOfCycle;
import com.healthymedium.arc.notifications.types.VisitNextDay;
import com.healthymedium.arc.notifications.types.VisitNextMonth;
import com.healthymedium.arc.notifications.types.VisitNextWeek;

public class NotificationTypes {

    public static TestProctor TestProctor = new TestProctor();
    public static TestConfirmed TestConfirmed = new TestConfirmed();
    public static TestMissed TestMissed = new TestMissed();
    public static TestNext TestNext = new TestNext();
    public static TestTake TestTake = new TestTake();
//    public static TestTakeFirstOfDay TestTakeFirstOfDay = new TestTakeFirstOfDay();
//    public static TestTakeSecondOfDay TestTakeSecondOfDay = new TestTakeSecondOfDay();
//    public static TestTakeThirdOfDay TestTakeThirdOfDay = new TestTakeThirdOfDay();
//    public static TestTakeLastOfDay TestTakeLastOfDay = new TestTakeLastOfDay();
//    public static TestTakeFirstOfDay4 TestTakeFirstOfDay4 = new TestTakeFirstOfDay4();
//    public static TestTakeFirstOfCycle TestTakeFirstOfCycle = new TestTakeFirstOfCycle();
//    public static TestTakeLastOfCycle TestTakeLastOfCycle = new TestTakeLastOfCycle();
    public static VisitNextDay VisitNextDay = new VisitNextDay();
    public static VisitNextWeek VisitNextWeek = new VisitNextWeek();
    public static VisitNextMonth VisitNextMonth = new VisitNextMonth();

}
