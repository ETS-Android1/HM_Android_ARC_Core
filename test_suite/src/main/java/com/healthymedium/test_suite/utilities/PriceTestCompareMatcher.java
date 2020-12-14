package com.healthymedium.test_suite.utilities;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.healthymedium.arc.ui.base.RoundedLinearLayout;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class PriceTestCompareMatcher  extends TypeSafeMatcher<View> {

    public static String price;
    public static String food;


    public PriceTestCompareMatcher() {
        super(View.class);
    }

    @Override public void describeTo(Description description) {
        description.appendText("Extract value from View for public access");
    }

    @Override
    protected boolean matchesSafely(View view) {
        if(view instanceof  RelativeLayout) {
            RelativeLayout group = (RelativeLayout) view;
            food = ((TextView) group.getChildAt(0)).getText().toString();
            price = ((TextView) group.getChildAt(1)).getText().toString();
            return true;
        }
        else if(view instanceof RoundedLinearLayout){
            RoundedLinearLayout group = (RoundedLinearLayout) view;
            food = ((TextView) group.getChildAt(0)).getText().toString();
            price = ((TextView) group.getChildAt(1)).getText().toString();
            return true;
        }
        return false;
    }
}