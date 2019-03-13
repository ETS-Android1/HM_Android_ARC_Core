package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.study.Visit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@SuppressLint("ValidFragment")
public class QuestionAdjustSchedule extends QuestionTemplate {

    boolean allowHelp;

    int index = 0;
    int shiftDays = 0;

    public QuestionAdjustSchedule(boolean allowBack, boolean allowHelp, String header, String subheader) {
        super(allowBack,header,subheader,"SUBMIT");
        this.allowHelp = allowHelp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(allowHelp);

        NumberPicker picker = new NumberPicker(Application.getInstance());
        content.addView(picker);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                index = newVal;
                enableNextButton();
            }
        });


        Participant participant = Study.getParticipant();
        Visit visit = participant.getCurrentVisit();

        DateTime visitStart = visit.getScheduledStartDate();
        DateTime visitEnd = visit.getScheduledEndDate();
        String start;
        String end;

        String[] data = new String[15];
        String range;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE, MMM d");

        // TODO
        // Generalize this at some point
        // Should allow arbitrary ranges

        // Back 7 days
        start = fmt.print(visitStart.minusDays(7));
        end = fmt.print(visitEnd.minusDays(7));
        range = start + "-" + end;
        data[0] = range;

        start = fmt.print(visitStart.minusDays(6));
        end = fmt.print(visitEnd.minusDays(6));
        range = start + "-" + end;
        data[1] = range;

        start = fmt.print(visitStart.minusDays(5));
        end = fmt.print(visitEnd.minusDays(5));
        range = start + "-" + end;
        data[2] = range;

        start = fmt.print(visitStart.minusDays(4));
        end = fmt.print(visitEnd.minusDays(4));
        range = start + "-" + end;
        data[3] = range;

        start = fmt.print(visitStart.minusDays(3));
        end = fmt.print(visitEnd.minusDays(3));
        range = start + "-" + end;
        data[4] = range;

        start = fmt.print(visitStart.minusDays(2));
        end = fmt.print(visitEnd.minusDays(2));
        range = start + "-" + end;
        data[5] = range;

        start = fmt.print(visitStart.minusDays(1));
        end = fmt.print(visitEnd.minusDays(1));
        range = start + "-" + end;
        data[6] = range;

        // Original range
        start = fmt.print(visitStart);
        end = fmt.print(visitEnd);
        range = start + "-" + end;
        data[7] = range;

        // Forward 7 days
        start = fmt.print(visitStart.plusDays(1));
        end = fmt.print(visitEnd.plusDays(1));
        range = start + "-" + end;
        data[8] = range;

        start = fmt.print(visitStart.plusDays(2));
        end = fmt.print(visitEnd.plusDays(2));
        range = start + "-" + end;
        data[9] = range;

        start = fmt.print(visitStart.plusDays(3));
        end = fmt.print(visitEnd.plusDays(3));
        range = start + "-" + end;
        data[10] = range;

        start = fmt.print(visitStart.plusDays(4));
        end = fmt.print(visitEnd.plusDays(4));
        range = start + "-" + end;
        data[11] = range;

        start = fmt.print(visitStart.plusDays(5));
        end = fmt.print(visitEnd.plusDays(5));
        range = start + "-" + end;
        data[12] = range;

        start = fmt.print(visitStart.plusDays(6));
        end = fmt.print(visitEnd.plusDays(6));
        range = start + "-" + end;
        data[13] = range;

        start = fmt.print(visitStart.plusDays(7));
        end = fmt.print(visitEnd.plusDays(7));
        range = start + "-" + end;
        data[14] = range;

        picker.setMinValue(0);
        picker.setMaxValue(data.length-1);
        picker.setDisplayedValues(data);

        return view;
    }

    @Override
    public Object onValueCollection(){

        // TODO
        // Make this not gross

        // Return the number of days shifted
        // 0 is -7 days
        // 7 is no change
        // 14 is +7 days

        if (index == 0) {
            return -7;
        } else if (index == 1) {
            return -6;
        } else if (index == 2) {
            return -5;
        } else if (index == 3) {
            return -4;
        } else if (index == 4) {
            return -3;
        } else if (index == 5) {
            return -2;
        } else if (index == 6) {
            return -1;
        } else if (index == 7) {
            return 0;
        } else if (index == 8) {
            return 1;
        } else if (index == 9) {
            return 2;
        } else if (index == 10) {
            return 3;
        } else if (index == 11) {
            return 4;
        } else if (index == 12) {
            return 5;
        } else if (index == 13) {
            return 6;
        } else if (index == 14) {
            return 7;
        }

        return null;
    }

    public void updateDates() {
        if (shiftDays == 0) {
            return;
        }

        Participant participant = Study.getParticipant();
        Visit visit = participant.getCurrentVisit();

        if (shiftDays < 0) {
            shiftDays = Math.abs(shiftDays);
            for (int i = 0; i < visit.testSessions.size(); i++) {
                TestSession temp = visit.testSessions.get(i);
                temp.setScheduledTime(temp.getScheduledTime().minusDays(shiftDays));
                visit.testSessions.set(i, temp);

                if (i == 0) {
                    visit.setScheduledStartDate(temp.getScheduledTime());
                } else if (i == visit.testSessions.size()-1) {
                    visit.setScheduledEndDate(temp.getScheduledTime());
                }
            }
        } else {
            for (int i = 0; i < visit.testSessions.size(); i++) {
                TestSession temp = visit.testSessions.get(i);
                temp.setScheduledTime(temp.getScheduledTime().plusDays(shiftDays));
                visit.testSessions.set(i, temp);

                if (i == 0) {
                    visit.setScheduledStartDate(temp.getScheduledTime());
                } else if (i == visit.testSessions.size()-1) {
                    visit.setScheduledEndDate(temp.getScheduledTime());
                }
            }
        }
    }

    public void enableNextButton() {
        if(!buttonNext.isEnabled()){
            buttonNext.setEnabled(true);
            onNextButtonEnabled(true);

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shiftDays = (int) onValueCollection();
                    updateDates();
                    onNextRequested();
                }
            });
        }
    }
}
