package com.healthymedium.arc.font;

import android.graphics.Typeface;

public class Fonts {

    private static boolean loaded = false;
    public static Typeface georgia;
    public static Typeface georgiaItalic;

    public static Typeface roboto;
    public static Typeface robotoMedium;
    public static Typeface robotoBold;

    public static void load(){
        if(loaded){
            return;
        }
        FontFactory fontFactory = FontFactory.getInstance();
        if(fontFactory != null) {
            georgia = fontFactory.getFont("fonts/Georgia.ttf");
            georgiaItalic = fontFactory.getFont("fonts/Georgia-Italic.ttf");

            roboto = fontFactory.getFont("fonts/Roboto-Regular.ttf");
            robotoMedium = fontFactory.getFont("fonts/Roboto-Medium.ttf");
            robotoBold = fontFactory.getFont("fonts/Roboto-Bold.ttf");

            loaded = true;
        }
    }

    public static boolean areLoaded() {
        return loaded;
    }
}
