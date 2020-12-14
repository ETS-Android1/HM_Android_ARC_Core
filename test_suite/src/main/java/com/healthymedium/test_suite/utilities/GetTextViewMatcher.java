package com.healthymedium.test_suite.utilities;

import androidx.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import com.healthymedium.arc.ui.RadioButton;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;


public class GetTextViewMatcher extends TypeSafeMatcher<View> {

    public static String value;

    public GetTextViewMatcher() {
        super(View.class);
    }

    @Override public void describeTo(Description description) {
        description.appendText("Extract value from View for public access");
    }

    @Override
    protected boolean matchesSafely(View view) {
        if(view instanceof TextView){
            value = ((TextView) view).getText().toString();
            return true;
        }

        if(view instanceof RadioButton){
            value = ((RadioButton) view).getText();
            return true;
        }


        return false;
    }
}