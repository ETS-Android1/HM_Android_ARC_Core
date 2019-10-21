package com.healthymedium.arc.study;

import com.healthymedium.arc.time.TimeUtil;
import com.healthymedium.arc.utilities.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class SchedulerTest {


    @Test
    public void testDefault() {

        Log.pointToSystemOut();
        Log.v("testDefault","\n--------------------------------------------------\n"+
                "one schedule");

        Participant participant = Participants.getDefault();

        DateTime now = DateTime.parse("2019-07-29 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        TestScheduler scheduler = new TestScheduler();
        scheduler.scheduleTests(now,participant);
    }

    @Test
    public void test() {

        Log.pointToSystemOut();
        Log.v("test","\n--------------------------------------------------\n");

        CircadianClock clock = new CircadianClock();
        clock.setRhythms("06:00:00","21:00:00");

        Participant participant = Participants.getDefault();
        participant.getState().circadianClock = clock;

        DateTime now = DateTime.parse("2019-07-29 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        TestScheduler scheduler = new TestScheduler();
        scheduler.scheduleTests(now,participant);

        clock.setRhythms("19:00:00","03:00:00");
//        participant.getState().circadianClock = CircadianClocks.getDefault2();

        scheduler.scheduleTests(now,participant);

//        DateTime then = DateTime.parse("2019-07-29 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));
//        DateTimeUtils.setCurrentMillisFixed(then.getMillis());



    }


    public class TestScheduler extends Scheduler {

        public TestScheduler() {
            super();
        }

        @Override
        public void initializeCycles(DateTime now, Participant participant) {

            List<TestCycle> cycles = participant.getState().testCycles;
            DateTime midnight = TimeUtil.setMidnight(now);

            TestCycle baseline = initializeBaselineCycle(midnight,4);
            cycles.add(baseline);

            TestCycle visit1 = initializeCycle(1,4,midnight.plusDays(90),3);
            cycles.add(visit1);

            int id = 0;
            for(TestCycle cycle : cycles) {
                for(TestDay day : cycle.getTestDays()) {
                    for(TestSession session : day.getTestSessions()) {
                        session.setId(id);
                        id++;
                    }
                }
            }

        }
    }

}