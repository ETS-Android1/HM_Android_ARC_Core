package com.healthymedium.arc.study;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CircadianClockTest {

    @Test
    public void getRhythmInstances(){

        CircadianClock clock = new CircadianClock();
        clock.getRhythm(0).setTimes("08:30:00","20:30:00"); // Sunday
        clock.getRhythm(1).setTimes("07:00:00","20:15:00"); // Monday
        clock.getRhythm(2).setTimes("07:30:00","21:00:00"); // Tuesday
        clock.getRhythm(3).setTimes("07:00:00","21:00:00"); // Wednesday
        clock.getRhythm(4).setTimes("07:30:00","21:00:00"); // Thursday
        clock.getRhythm(5).setTimes("07:00:00","20:15:00"); // Friday
        clock.getRhythm(6).setTimes("09:30:00","23:15:00"); // Saturday

        clock.getRhythmInstances(LocalDate.now(),10);
    }

}