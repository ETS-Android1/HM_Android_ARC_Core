package com.healthymedium.arc.api.models;

import java.util.List;

public class CycleProgress {

    public Integer cycle;
    public Long start_date;
    public Long end_date;
    public Integer day_count;
    public Integer current_day;
    public List<DayProgress> days;

}
