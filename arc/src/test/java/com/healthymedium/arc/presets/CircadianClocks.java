package com.healthymedium.arc.presets;

import com.healthymedium.arc.study.CircadianClock;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class CircadianClocks {

    public static CircadianClock getDefault(){
        CircadianClock clock = new CircadianClock();
        clock.getRhythm(0).setTimes("08:00:00","17:00:00"); // Sunday
        clock.getRhythm(1).setTimes("08:00:00","17:00:00"); // Monday
        clock.getRhythm(2).setTimes("08:00:00","17:00:00"); // Tuesday
        clock.getRhythm(3).setTimes("08:00:00","17:00:00"); // Wednesday
        clock.getRhythm(4).setTimes("08:00:00","17:00:00"); // Thursday
        clock.getRhythm(5).setTimes("08:00:00","17:00:00"); // Friday
        clock.getRhythm(6).setTimes("08:00:00","17:00:00"); // Saturday
        Assert.assertTrue(clock.isValid());
        return clock;
    }

}