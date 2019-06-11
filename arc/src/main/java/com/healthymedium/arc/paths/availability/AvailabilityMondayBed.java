package com.healthymedium.arc.paths.availability;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.Hours;
import org.joda.time.LocalTime;

@SuppressLint("ValidFragment")
public class AvailabilityMondayBed extends QuestionTime {

    CircadianClock clock;
    int minWakeTime = 4;
    int maxWakeTime = 24;

    public AvailabilityMondayBed() {
        super(true, ViewUtil.getString(R.string.availability_sleep_monday),"",null);
    }

    public AvailabilityMondayBed(int minWakeTime, int maxWakeTime) {
        super(true, ViewUtil.getString(R.string.availability_sleep_monday),"",null);
        this.minWakeTime = minWakeTime;
        this.maxWakeTime = maxWakeTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        if(time==null && clock.hasBedRhythmChanged("Monday")) {
            time = clock.getRhythm("Monday").getBedTime();
        }

        LocalTime wakeTime = clock.getRhythm("Monday").getWakeTime();
        timeInput.placeRestrictions(wakeTime, minWakeTime, maxWakeTime);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm("Monday").setBedTime(timeInput.getTime());
                Study.openNextFragment();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        Study.getParticipant().save();
        super.onPause();
    }

    @Override
    public Object onDataCollection(){
        return null;
    }

}
