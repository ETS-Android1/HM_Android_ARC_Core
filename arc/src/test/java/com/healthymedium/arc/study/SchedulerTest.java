package com.healthymedium.arc.study;

import com.healthymedium.arc.presets.CircadianClocks;
import com.healthymedium.arc.presets.Participants;
import com.healthymedium.arc.utilities.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SchedulerTest {


    @Test
    public void testDefault() {

        Log.pointToSystemOut();

        Participant participant = Participants.getDefault();

        DateTime now = DateTime.parse("2019-07-29 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        Scheduler scheduler = new Scheduler();
        scheduler.scheduleTests(now,participant);
    }

}