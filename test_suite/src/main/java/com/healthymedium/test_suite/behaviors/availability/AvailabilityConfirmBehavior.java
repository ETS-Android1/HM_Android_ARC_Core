package com.healthymedium.test_suite.behaviors.availability;

import androidx.test.espresso.matcher.ViewMatchers;

import com.healthymedium.test_suite.core.TestBehavior;
import com.healthymedium.test_suite.data.CircadianClocks;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.CircadianRhythm;

import org.joda.time.LocalTime;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;


public class AvailabilityConfirmBehavior extends Behavior {


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);

       CircadianClock clock = CircadianClocks.get(TestBehavior.availability.wake,TestBehavior.availability.bed);

        boolean same = true;

        List<CircadianRhythm> rhythms = clock.getRhythms();

        LocalTime wake = rhythms.get(1).getWakeTime(); // Monday
        LocalTime bed = rhythms.get(1).getBedTime(); // Monday

        // check if Monday through Friday are the same
        int size = rhythms.size()-1;
        for(int i=1;i<size;i++){
            if(!wake.equals(rhythms.get(i).getWakeTime())){
                same = false;
                break;
            }
            if(!bed.equals(rhythms.get(i).getBedTime())){
                same = false;
                break;
            }
        }

        int stringId = same ? R.string.radio_yes : R.string.radio_no;
        onView(ViewMatchers.withText(stringId)).perform(click());
        UI.sleep(400);
        UI.click(R.id.buttonNext);
    }

}
