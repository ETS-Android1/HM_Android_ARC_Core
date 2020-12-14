package com.healthymedium.test_suite.behaviors.questions;

import androidx.test.espresso.matcher.ViewMatchers;

import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.arc.core.BaseFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import com.healthymedium.arc.library.R;


public class QuestionLanguageBehavior extends Behavior {


    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        onView(ViewMatchers.withText("United States - English")).perform(click());
        onView(withId(R.id.button)).perform(click());
    }

}
