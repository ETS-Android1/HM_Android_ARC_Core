package com.healthymedium.test_suite.data;

import com.healthymedium.arc.study.CircadianClock;
import org.joda.time.LocalTime;

public class CircadianClocks {

    public static CircadianClock get(LocalTime wake, LocalTime bed){
        CircadianClock clock = new CircadianClock();
        clock.setRhythms(wake,bed);
        return clock;
    }

    public static CircadianClock get(String wake, String bed){
        CircadianClock clock = new CircadianClock();
        clock.setRhythms(wake,bed);
        return clock;
    }

    public static CircadianClock getDefault(){
        return get("08:00","19:00");
    }

}
