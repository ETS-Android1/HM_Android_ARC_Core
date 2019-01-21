package com.healthymedium.arc.study;

import org.joda.time.DateTime;

public class CircadianInstant {

    private DateTime bed;
    private DateTime wake;

    CircadianInstant(){
        bed = DateTime.now();
        wake = DateTime.now();
    }

    public DateTime getBedTime() {
        return bed;
    }

    public void setBedTime(DateTime bed) {
        this.bed = bed;
    }

    public DateTime getWakeTime() {
        return wake;
    }

    public void setWakeTime(DateTime wake) {
        this.wake = wake;
    }

    public boolean isNocturnal(){
        return (wake.isAfter(bed));
    }

}
