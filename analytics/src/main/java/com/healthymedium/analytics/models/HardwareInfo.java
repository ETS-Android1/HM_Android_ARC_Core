package com.healthymedium.analytics.models;

import android.os.Build;
import android.text.TextUtils;

public class HardwareInfo {

    private String manufacturer = Build.MANUFACTURER;
    private String product = Build.PRODUCT;
    private String model = Build.MODEL;
    private String brand = Build.BRAND;
    private String board = Build.BOARD;
    private String id = Build.ID;
    private String name;

    public HardwareInfo() {
        name = getDeviceName();
    }

    private String getDeviceName() {
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

}
