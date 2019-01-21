package com.healthymedium.arc.font;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;
import java.util.List;

public class FontFactory {

    static FontFactory instance;
    Context context;

    FontFactory(Context context){
        instance = this;
        instance.context = context;
    }

    public static synchronized void initialize(Context context) {
        instance = new FontFactory(context);
    }

    public static FontFactory getInstance(){
        return instance;
    }


    public void setDefaultFont(Typeface typeface) {
        replaceFont("DEFAULT", typeface);
    }

    public void setDefaultBoldFont(Typeface typeface) {
        replaceFont("DEFAULT_BOLD", typeface);
    }

    public Typeface getDefaultFont() {
        return Typeface.DEFAULT;
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Typeface getFont(String path){
        return Typeface.createFromAsset(context.getResources().getAssets(), path);
    }
}
