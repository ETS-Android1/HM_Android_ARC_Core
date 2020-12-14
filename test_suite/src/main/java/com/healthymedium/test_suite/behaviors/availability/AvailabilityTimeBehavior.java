package com.healthymedium.test_suite.behaviors.availability;

import androidx.test.espresso.ViewAction;

import com.healthymedium.arc.paths.availability.AvailabilityBed;
import com.healthymedium.arc.paths.availability.AvailabilityWake;
import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.data.CircadianClocks;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.ViewActions;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.CircadianClock;

import org.junit.Assert;

import org.joda.time.LocalTime;

public class AvailabilityTimeBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(500);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "opened");

        CircadianClock clock = CircadianClocks.get(TestBehavior.availability.wake,TestBehavior.availability.bed);
        LocalTime time = null;

        boolean wake = false;

        Class tClass = fragment.getClass();

        if(tClass.equals(AvailabilityWake.class)){
            wake = true;

        } else if(tClass.equals(AvailabilityBed.class)){
            wake = false;
        }

        if(wake){
            time = clock.getRhythm(0).getWakeTime();
        } else {
            time = clock.getRhythm(0).getBedTime();
        }

        Assert.assertNotNull("invalid local time",time);

        ViewAction setTime = ViewActions.setTime(time.getHourOfDay(),time.getMinuteOfHour());
        UI.getTimePicker().perform(setTime);
        UI.sleep(400);
        Capture.takeScreenshot(fragment, getClass().getSimpleName(), "setTime");
        UI.click(R.id.buttonNext);
    }

}
