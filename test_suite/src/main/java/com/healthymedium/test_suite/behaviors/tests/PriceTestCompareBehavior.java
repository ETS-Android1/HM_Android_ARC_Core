package com.healthymedium.test_suite.behaviors.tests;

import android.content.res.Resources;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.util.Log;
import android.view.View;


import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.TestVariant;
import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.PriceTestCompareMatcher;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import org.hamcrest.Matcher;

import java.util.ArrayList;


public class PriceTestCompareBehavior extends Behavior {

    String pricePrefix;
    String priceSuffix;
    public static ArrayList<String> items = null;
    public static ArrayList<String> prices = null;
    private ArrayList<String> both = null;
    String TAG = getClass().getSimpleName();

    @Override
    public void onOpened(BaseFragment fragment) {
        super.onOpened(fragment);

        Resources resources=  InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        pricePrefix = resources.getString(R.string.money_prefix);
        priceSuffix = resources.getString(R.string.money_suffix);

        items = new ArrayList<>();
        prices = new ArrayList<>();
        both = new ArrayList<>();
        String textViewFood;
        String textViewPrice;


        int index = 1;
        while(index <= 10) {
            do{
                onView(withId(R.id.fragmentPriceTestCompare)).check(matches(new PriceTestCompareMatcher()));
                textViewFood = PriceTestCompareMatcher.food;
                textViewPrice = PriceTestCompareMatcher.price;
            }
            while (both.contains(textViewFood+textViewPrice));


            both.add(textViewFood+textViewPrice);


            String currItem;
            String currPrice;
            if (textViewFood.contains(pricePrefix) && textViewFood.contains(priceSuffix)) {
                currItem = textViewPrice;
                currPrice = textViewFood;

            } else {
                currItem = textViewFood;
                currPrice = textViewPrice;
            }
            UI.sleep(500);
            Capture.takeScreenshot(fragment, TAG, "test_" + index);
            items.add(currItem);
            prices.add(currPrice);
            index++;

        }



        for(int a = 0; a < 10; a++){
            Log.e(TAG,a + "  " + items.get(a) + " - " + prices.get(a));

        }
        UI.sleep(3000);
        return;
    }





}
