package com.healthymedium.arc.study;

import android.content.Context;
import android.util.Pair;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CircadianClock {

    private DateTime createdOn;

    private List<CircadianRhythm> rhythms = new ArrayList<>();

    public CircadianClock(){
        rhythms.add(new CircadianRhythm("Sunday"));
        rhythms.add(new CircadianRhythm("Monday"));
        rhythms.add(new CircadianRhythm("Tuesday"));
        rhythms.add(new CircadianRhythm("Wednesday"));
        rhythms.add(new CircadianRhythm("Thursday"));
        rhythms.add(new CircadianRhythm("Friday"));
        rhythms.add(new CircadianRhythm("Saturday"));
        createdOn = DateTime.now();
    }

    public List<CircadianRhythm> getRhythms() {
        return Collections.unmodifiableList(rhythms);
    }

    public CircadianRhythm getRhythm(int index) {
        return rhythms.get(index);
    }

    public CircadianRhythm getRhythm(String weekday) {
        for(CircadianRhythm rhythm : rhythms){
            if(rhythm.getWeekday().equals(weekday)){
                return rhythm;
            }
        }
        return new CircadianRhythm("");
    }

    public boolean hasBedRhythmChanged(int index) {
        return getRhythm(index).lastUpdatedBedOn().isAfter(createdOn);
    }

    public boolean hasWakeRhythmChanged(int index) {
        return getRhythm(index).lastUpdatedWakedOn().isAfter(createdOn);
    }

    public boolean hasBedRhythmChanged(String weekday) {
        return getRhythm(weekday).lastUpdatedBedOn().isAfter(createdOn);
    }

    public boolean hasWakeRhythmChanged(String weekday) {
        return getRhythm(weekday).lastUpdatedWakedOn().isAfter(createdOn);
    }

    public int getRhythmIndex(String weekday) {
        int size = rhythms.size();
        for(int i=0;i<size;i++){
            if(rhythms.get(i).getWeekday().equals(weekday)){
                return i;
            }
        }
        return 0;
    }

    public void setRhythms(List<CircadianRhythm> rhythms) {
        this.rhythms = rhythms;
    }

    public boolean isValid(){
        List<CircadianInstant> orderedRhythms = getRhythmInstances(LocalDate.now());
        List<DateTime> mockDates = new ArrayList<>();

        int size = orderedRhythms.size();
        if(size == 0){
            return false;
        }

        for(int i=0;i<size;i++){
            mockDates.add(orderedRhythms.get(i).getWakeTime());
            mockDates.add(orderedRhythms.get(i).getBedTime());
        }

        size = mockDates.size();

        for(int i=1;i<size;i++){
            if(mockDates.get(i).isBefore(mockDates.get(i-1))) {
                return false;
            }
        }

        return true;
    }


    // this outputs a list of pairs<wake,sleep>
    // the list starts with the date provided and provides a weeks worth of pairs
    public List<CircadianInstant> getRhythmInstances(LocalDate startDate){

        List<CircadianInstant> orderedRhythms = new ArrayList<>();
        int rhythmCount = rhythms.size();

        if(rhythmCount==0){
            return orderedRhythms;
        }

        List<DateTime> dateTimeList = new ArrayList<>();
        boolean wrapAround = rhythms.get(0).isNocturnal();
        int startIndex = startDate.getDayOfWeek();

        for(int i=startIndex;i<rhythmCount;i++){
            CircadianInstant instant = new CircadianInstant();
            DateTime wake;
            DateTime bed;

            if(rhythms.get(i).isNocturnal()){
                wake = startDate.toDateTime(rhythms.get(i).getWakeTime());
                bed = startDate.toDateTime(rhythms.get(i).getBedTime()).plusDays(1);
            } else {
                wake = startDate.toDateTime(rhythms.get(i).getWakeTime());
                bed = startDate.toDateTime(rhythms.get(i).getBedTime());
            }
            instant.setWakeTime(wake);
            instant.setBedTime(bed);

            orderedRhythms.add(instant);
            startDate = startDate.plusDays(1);
        }

        for(int i=0;i<startIndex;i++){
            CircadianInstant instant = new CircadianInstant();
            DateTime wake;
            DateTime bed;

            if(rhythms.get(i).isNocturnal()){
                wake = startDate.toDateTime(rhythms.get(i).getWakeTime());
                bed = startDate.toDateTime(rhythms.get(i).getBedTime()).plusDays(1);
            } else {
                wake = startDate.toDateTime(rhythms.get(i).getWakeTime());
                bed = startDate.toDateTime(rhythms.get(i).getBedTime());
            }
            instant.setWakeTime(wake);
            instant.setBedTime(bed);

            orderedRhythms.add(instant);
            startDate = startDate.plusDays(1);
        }

        return orderedRhythms;

    }

    private int getNextIndex(int index){
        index++;
        if(index >= rhythms.size()){
            index = 0;
        };
        return index;
    }

    private int getPreviousIndex(int index){
        index--;
        if(index < 0){
            index = rhythms.size()-1;
        };
        return index;
    }


}
