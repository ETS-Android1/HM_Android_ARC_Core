package com.healthymedium.test_suite.utilities;

import androidx.annotation.IdRes;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import android.widget.TimePicker;

import org.hamcrest.Matchers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;

public class UI {

    public static void click(@IdRes int id) {
        onView(ViewMatchers.withId(id)).perform(ViewActions.click());
    }

    public static void click(String text) {
        onView(ViewMatchers.withText(text)).perform(ViewActions.click());
    }

    // ---------------------------------------------------------------------------------------------

    public static ViewInteraction getTimePicker() {
        return onView(withClassName(Matchers.equalTo(TimePicker.class.getName())));
    }

    // ---------------------------------------------------------------------------------------------

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
