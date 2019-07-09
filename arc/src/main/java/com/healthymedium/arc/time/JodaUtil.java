package com.healthymedium.arc.time;


import android.support.annotation.StringRes;

import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import java.util.Locale;


public class JodaUtil {

    public static double toUtcDouble(DateTime dateTime){
        double utc = dateTime.getMillis();
        return utc/1000;
    }

    public static DateTime fromUtcDouble(double dateTime){
        long longTime = (long)(dateTime*1000L);
        return new DateTime(longTime);
    }

    public static String format(DateTime dateTime, @StringRes int format){
        return dateTime.toString(ViewUtil.getString(format));
    }

    public static String format(DateTime dateTime, @StringRes int format, Locale locale){
        return dateTime.toString(ViewUtil.getString(format),locale);
    }

    public static DateTime setTime(DateTime date, String time){
        String[] split = time.split("[: ]");
        if(split.length != 3){
            return date;
        }
        split[2]  = split[2].toLowerCase();
        int hours = Integer.valueOf(split[0]);
        int minutes = Integer.valueOf(split[1]);
        if(split[2].toLowerCase().equals("pm") && hours != 12){
            hours +=12;
        } else if(split[2].toLowerCase().equals("am") && hours == 12){
            hours = 0;
        } else {
            //hours -= 1;
        }
        DateTime newDate = date
                .withHourOfDay(hours)
                .withMinuteOfHour(minutes)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return newDate;
    }

    public static DateTime setMidnight(DateTime date){
        DateTime newDate = date
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return newDate;
    }


}
