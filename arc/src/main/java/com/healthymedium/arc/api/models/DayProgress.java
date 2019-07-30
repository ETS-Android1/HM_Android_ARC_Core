package com.healthymedium.arc.api.models;

import java.util.List;

public class DayProgress {

    public Long start_date;
    public Long end_date;
    public Integer day;
    public Integer cycle;
    public List<SessionProgress> sessions;

}
