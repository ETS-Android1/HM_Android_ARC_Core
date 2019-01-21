package com.healthymedium.arc.api.models;

public class SessionInfo {

    public int session_date;   // scheduledTime
    public int week;           // Weeks.weeksBetween(visits.get(0).getScheduledStartDate(),visit.getScheduledStartDate()).getWeeks();
    public int day;            // day index
    public int session;        // session.getIndex()%visit.getNumberOfTests(session.getDayIndex());
    public int session_id;     //

}
