package com.healthymedium.test_suite.core;

import android.content.Intent;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.healthymedium.arc.core.MainActivity;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BaseTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule;


    @Before
    public void before() throws Exception {
        PreferencesManager.getInstance().removeAll();
        CacheManager.getInstance().removeAll();
    }

    public void launchActivity() {
        activityTestRule = new ActivityTestRule<>(TestBehavior.classes.activity, true, false);
        activityTestRule.launchActivity(new Intent());
    }

    @After
    public void after(){
        PreferencesManager.getInstance().removeAll();
        CacheManager.getInstance().removeAll();
    }

}
