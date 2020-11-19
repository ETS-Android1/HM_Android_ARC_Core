package com.healthymedium.arc.paths.availability;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;

import org.joda.time.Hours;
import org.joda.time.LocalTime;

@SuppressLint("ValidFragment")
public class AvailabilityOtherBed extends QuestionTime {

    CircadianClock clock;
    String weekday;
    int minWakeTime = 4;
    int maxWakeTime = 24;

    @SuppressLint("ValidFragment")
    public AvailabilityOtherBed(String weekday, String header) {
        super(true, header,"",null);
        this.weekday = weekday;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        if (getArguments() != null) {
            if (getArguments().containsKey("minWakeTime")) {
                minWakeTime = getArguments().getInt("minWakeTime");
            }

            if (getArguments().containsKey("maxWakeTime")) {
                maxWakeTime = getArguments().getInt("maxWakeTime");
            }
        }


        clock = Study.getParticipant().getCircadianClock();
        if(time==null && !clock.hasBedRhythmChanged(weekday)){
            int index = clock.getRhythmIndex(weekday)-1;
            time = clock.getRhythm(index).getBedTime();
        } else if(time==null){
            time = clock.getRhythm(weekday).getBedTime();
        }

        LocalTime wakeTime = clock.getRhythm(weekday).getWakeTime();
        timeInput.placeRestrictions(wakeTime, minWakeTime, maxWakeTime);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm(weekday).setBedTime(timeInput.getTime());
                Study.openNextFragment();
            }
        });

        return view;
    }

    public String getWeekday(){
        return weekday;
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
