package com.healthymedium.test_suite.data;

import com.healthymedium.arc.api.tests.BaseTest;

public class TestData extends BaseTest {

    private int progress = 0;
    public TestData(int p){
        progress = p;
    }

    @Override
    public int getProgress(boolean testCompleted) {
        if (testCompleted) {
            return 100;
        } else {
            return progress;
        }
    }
}
