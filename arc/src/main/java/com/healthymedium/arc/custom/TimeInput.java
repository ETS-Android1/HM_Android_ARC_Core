package com.healthymedium.arc.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.healthymedium.arc.library.R;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalTime;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeInput extends FrameLayout {

    boolean restrictTime = false;
    boolean valid = true;

    LocalTime blockoutBegin;
    LocalTime blockoutEnd;
    LocalTime time;

    int minWakeTime = 4;
    int maxWakeTime = 24;

    Listener listener;
    TimePicker timePicker;
    TextView errorText;

    public TimeInput(Context context) {
        super(context);
        init(context);
    }

    public TimeInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_time_input,this);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setHour(12);
        timePicker.setMinute(0);

        errorText = view.findViewById(R.id.errorText);

        time = new LocalTime(12,0);


        try{
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field minute = classForid.getField("minute");
            NumberPicker minuteSpinner = timePicker.findViewById(minute.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / 15) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += 15) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(listener!=null){
                    listener.onTimeChanged();
                }
                if(restrictTime){
                    time = new LocalTime(hourOfDay,minute*15);
                    if(time.isAfter(blockoutBegin) && time.isBefore(blockoutEnd)){
                        errorText.setText("Please set a minimum of " + Integer.toString(minWakeTime) +" hours of wake time.");
                        setValidity(false);
                        return;
                    }

                    Hours hoursDiff = Hours.hoursBetween(blockoutBegin, time);
                    if (hoursDiff.getHours() > 18) {
                        errorText.setText("Please set a maximum of " + Integer.toString(maxWakeTime) + " hours of wake time.");
                        setValidity(false);
                        return;
                    }
                }
                setValidity(true);
            }
        });

    }

    private void setValidity(boolean valid){
        if(this.valid!=valid){
            this.valid = valid;

            int visibility = (valid) ? View.GONE:View.VISIBLE;
            errorText.setVisibility(visibility);

            if(listener!=null){
                listener.onValidityChanged(valid);
            }
        }
    }

    public void setTime(LocalTime localTime) {
        this.time = localTime;
        timePicker.setHour(localTime.getHourOfDay());
        timePicker.setMinute(localTime.getMinuteOfHour()/15);
    }

    public LocalTime getTime(){
        time = new LocalTime(timePicker.getHour(),timePicker.getMinute()*15);
        return time;
    }

    public boolean isTimeValid() {
        return valid;
    }

    public void placeRestrictions(LocalTime blockoutBegin, LocalTime blockoutEnd){
        restrictTime = true;
        this.blockoutBegin = blockoutBegin.minusMinutes(1);
        this.blockoutEnd = blockoutEnd;
        boolean notValid = time.isAfter(this.blockoutBegin) && time.isBefore(this.blockoutEnd);
        setValidity(!notValid);
    }

    public void placeRestrictions(LocalTime blockoutBegin, int minDuration, int maxDuration){
        Hours minDurationHours;

        minWakeTime = minDuration;
        maxWakeTime = maxDuration;

        if (minDuration == 0) {
            minDurationHours = Hours.ZERO;
        } else if (minDuration == 1) {
            minDurationHours = Hours.ONE;
        } else if (minDuration == 2) {
            minDurationHours = Hours.TWO;
        } else if (minDuration == 3) {
            minDurationHours = Hours.THREE;
        } else if (minDuration == 4) {
            minDurationHours = Hours.FOUR;
        } else if (minDuration == 5) {
            minDurationHours = Hours.FIVE;
        } else if (minDuration == 6) {
            minDurationHours = Hours.SIX;
        } else if (minDuration == 7) {
            minDurationHours = Hours.SEVEN;
        } else if (minDuration == 8) {
            minDurationHours = Hours.EIGHT;
        } else {
            minDurationHours = Hours.FOUR;
        }

        placeRestrictions(blockoutBegin,blockoutBegin.plus(minDurationHours));
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public interface Listener{
        void onValidityChanged(boolean valid);
        void onTimeChanged();
    }

}
