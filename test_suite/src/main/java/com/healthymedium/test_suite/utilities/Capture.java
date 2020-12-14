package com.healthymedium.test_suite.utilities;

import android.os.Environment;
import androidx.test.runner.screenshot.ScreenCapture;
import androidx.test.runner.screenshot.ScreenCaptureProcessor;
import androidx.test.runner.screenshot.Screenshot;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.analytics.Log;
import com.healthymedium.test_suite.core.TestBehavior;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;


public class Capture {

    public static int INDEX = 0;


    public static void takeScreenshot(BaseFragment fragment, String behaviorName, String actionName){

        String dirName = TestBehavior.testName;
        String formattedIndex = String.format(Locale.ENGLISH, "%04d", INDEX);
        String captureName = dirName +  "/"+ formattedIndex+ "_" +fragment.getSimpleTag() + "_" + behaviorName + "_" + actionName;
        INDEX++;

        try {

            File dir = new File(Environment.getExternalStorageDirectory().toString() , "Pictures/screenshots/" + dirName);
            if (!dir.exists()) {
                if(!dir.mkdirs()){
                    Log.e("Capture", "Failed to make the directory: " + dir.toString());
                }
            }


            ScreenCapture screenCapture = Screenshot.capture();
            screenCapture.setName(captureName);

            HashSet<ScreenCaptureProcessor> processors = new HashSet<>();
            processors.add(new CustomScreenCaptureProcessor());
            screenCapture.process(processors);
        } catch (IOException e) {
            Log.e("Capture", "Failed to process the screenshot: " + captureName);
            e.printStackTrace();
        }
    }
}
