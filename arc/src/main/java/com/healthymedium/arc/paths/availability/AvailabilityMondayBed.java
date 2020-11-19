package com.healthymedium.arc.paths.availability;

import android.os.Bundle;
import androidx.annotation.Nullable;
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

public class AvailabilityMondayBed extends QuestionTime {

    CircadianClock clock;
    int minWakeTime = 4;
    int maxWakeTime = 24;

    public AvailabilityMondayBed() {
        super(true, ViewUtil.getString(R.string.availability_sleep_monday),"",null);
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
