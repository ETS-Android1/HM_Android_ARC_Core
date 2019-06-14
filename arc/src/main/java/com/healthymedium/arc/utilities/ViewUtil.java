package com.healthymedium.arc.utilities;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;

public class ViewUtil {

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(int sp) {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static int getColor(@ColorRes int id){
        return ContextCompat.getColor(Application.getInstance(),id);
    }

    public static Drawable getDrawable(@DrawableRes int id){
        return ContextCompat.getDrawable(Application.getInstance(),id);
    }

    public static String getString(@StringRes int id){
        return Application.getInstance().getString(id);
    }

    public static String getStringConcat(@StringRes int ... ids){
        String string = new String();
        for(int i=0;i<ids.length;i++){
            string += Application.getInstance().getString(ids[i]);
        }
        return string;
    }

    public static String replaceToken(String input, @StringRes int format, String replacement){
        return input.replace(getString(format), replacement);
    }

    public static void underlineTextView(TextView textView){
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

}
