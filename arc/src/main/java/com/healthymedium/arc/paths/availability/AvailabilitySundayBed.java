package com.healthymedium.arc.paths.availability;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.paths.questions.QuestionTime;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.Study;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalTime;

public class AvailabilitySundayBed extends QuestionTime {

    CircadianClock clock;
    int endTimeRestriction = 4;
    boolean reschedule = false;

    public AvailabilitySundayBed() {
        super(true,"When do you usually<br/><b>go to bed</b> on <b>Sunday</b>?","",null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        if (getArguments() != null) {
            if (getArguments().containsKey("availabilityWindow")) {
                endTimeRestriction = getArguments().getInt("availabilityWindow");
            }

            if (getArguments().containsKey("reschedule")) {
                reschedule = getArguments().getBoolean("reschedule");
            }
        }

        clock = Study.getParticipant().getCircadianClock();
        if(time==null && !clock.hasBedRhythmChanged("Sunday")){
            time = clock.getRhythm("Saturday").getBedTime();
        } else if(time==null){
            time = clock.getRhythm("Sunday").getBedTime();
        }

        LocalTime wakeTime = clock.getRhythm("Sunday").getWakeTime();
        timeInput.placeRestrictions(wakeTime, endTimeRestriction);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNext.setEnabled(false);
                clock.getRhythm("Sunday").setBedTime(timeInput.getTime());
                AsyncLoader loader = new AsyncLoader();
                loader.execute();
            }
        });

        return view;
    }

    private class AsyncLoader extends AsyncTask<Void, Void, Void> {
        LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = new LoadingDialog();
            loadingDialog.show(getFragmentManager(),"LoadingDialog");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Study.getParticipant().setCircadianClock(clock);
            Study.getParticipant().save();
            Study.getRestClient().submitWakeSleepSchedule();

            DateTime start = Study.getParticipant().getState().studyStartDate;
            if(start==null){
                start = DateTime.now();
            }

            if (reschedule == true) {
                Study.getScheduler().rescheduleTests(start,Study.getInstance().getParticipant());
            } else {
                Study.getScheduler().scheduleTests(start,Study.getInstance().getParticipant());
            }
            Study.getRestClient().submitTestSchedule();
            Study.getScheduler().scheduleNotifications(Study.getCurrentVisit());
            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            loadingDialog.dismiss();
            Study.openNextFragment();
        }
    }

    @Override
    public Object onDataCollection(){
        return null;
    }

}
