package com.healthymedium.arc.api.models;

import java.util.List;

public class TestScheduleSession {

    public String session_id;      // an identifier for this specific session w/r/t the entire test. On iOS, we're just using the sessions "index", so to speak
    public Double session_date;    // the  date/time when this session is scheduled to start
    public Integer week;           // 0-indexed week that this session takes place in
    public Integer day;            // 0-indexed day within the current week
    public Integer session;        // 0-indexed session within the current day
    public List<String> types;     // test data objects

}
