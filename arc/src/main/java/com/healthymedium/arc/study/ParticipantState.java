package com.healthymedium.arc.study;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ParticipantState {

    public String id;
    public int currentTestSession;
    public int currentVisit;
    public List<Visit> visits;
    public CircadianClock circadianClock;
    public DateTime studyStartDate;
    public boolean hasValidSchedule;
    public boolean isStudyRunning;


    // These are variables only used during runtime
    public DateTime lastPauseTime;

    public ParticipantState(){
        circadianClock = new CircadianClock();
        lastPauseTime = new DateTime();
        visits = new ArrayList<>();
    }

}
