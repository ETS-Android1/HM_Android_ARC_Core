package com.healthymedium.test_suite.data;

import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Scheduler;

import org.joda.time.DateTime;

public class ParticipantStates {

    public static ParticipantState getDefault(Scheduler scheduler) {
       return getDefault(scheduler, DateTime.now());
    }

    public static ParticipantState getInitState(){
        Participant participant = new Participant();
        participant.initialize();
        return participant.getState();
    }

    public static ParticipantState getDefault(Scheduler scheduler, DateTime time) {
        Participant participant = new Participant();
        participant.initialize();
        participant.setCircadianClock(CircadianClocks.getDefault());
        participant.getState().id = "123456";
        scheduler.scheduleTests(time,participant);
        return participant.getState();
    }
}
