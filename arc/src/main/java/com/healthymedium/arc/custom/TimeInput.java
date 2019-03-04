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

    public void placeRestrictions(LocalTime blockoutBegin, int duration){
        Hours durationHours;

        if (duration == 0) {
            durationHours = Hours.ZERO;
        } else if (duration == 1) {
            durationHours = Hours.ONE;
        } else if (duration == 2) {
            durationHours = Hours.TWO;
        } else if (duration == 3) {
            durationHours = Hours.THREE;
        } else if (duration == 4) {
            durationHours = Hours.FOUR;
        } else if (duration == 5) {
            durationHours = Hours.FIVE;
        } else if (duration == 6) {
            durationHours = Hours.SIX;
        } else if (duration == 7) {
            durationHours = Hours.SEVEN;
        } else if (duration == 8) {
            durationHours = Hours.EIGHT;
        } else {
            durationHours = Hours.FOUR;
        }

        placeRestrictions(blockoutBegin,blockoutBegin.plus(durationHours));
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public interface Listener{
        void onValidityChanged(boolean valid);
        void onTimeChanged();
    }

}
