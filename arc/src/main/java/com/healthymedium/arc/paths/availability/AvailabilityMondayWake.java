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

public class AvailabilityMondayWake extends QuestionTime {

    CircadianClock clock;

    public AvailabilityMondayWake() {
        super(true, ViewUtil.getString(R.string.availability_wake_monday),"",null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        if(clock==null){
            clock = new CircadianClock();
        }

        if(time==null && clock.hasWakeRhythmChanged("Monday")) {
            time = clock.getRhythm("Monday").getWakeTime();
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm("Monday").setWakeTime(timeInput.getTime());
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
