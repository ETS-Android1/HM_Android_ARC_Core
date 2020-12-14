package com.healthymedium.test_suite.utilities;

import androidx.test.runner.screenshot.BasicScreenCaptureProcessor;

/** Custom processor that does not include any additional suffix with the filename */
public class CustomScreenCaptureProcessor extends BasicScreenCaptureProcessor {


    protected String getFilename(String prefix) {
        return prefix;
    }
}