package com.healthymedium.arc.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;

public class ViewUtil {

    private static int navBarHeight = -1;
    private static int statusBarHeight = -1;

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dpToPx(float dp) {
        return (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(int sp) {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static float mmToPx(float mm) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, mm, Resources.getSystem().getDisplayMetrics());
    }

    public static int mmToPx(int mm) {
        return (int) mmToPx((float)mm);
    }

    public static float inToPx(float in) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, in, Resources.getSystem().getDisplayMetrics());
    }

    public static int inToPx(int in) {
        return (int) inToPx((float)in);
    }

    public static int getColor(Context context, @ColorRes int id){
        return ContextCompat.getColor(context,id);
    }

    public static int getColor(@ColorRes int id){
        return ContextCompat.getColor(Application.getInstance().getAppContext(),id);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int id){
        return ContextCompat.getDrawable(context,id);
    }

    public static Drawable getDrawable(@DrawableRes int id){
        return ContextCompat.getDrawable(Application.getInstance().getAppContext(),id);
    }

    public static String getString(Context context, @StringRes int id){
        return context.getString(id);
    }

    public static String getHtmlString(@StringRes int id) {
        return Html.fromHtml(Application.getInstance().getAppContext().getString(id)).toString();
    }

    public static String getString(@StringRes int id){
        return Application.getInstance().getAppContext().getString(id);
    }

    public static String getStringConcat(@StringRes int ... ids){
        String string = new String();
        for(int i=0;i<ids.length;i++){
            string += Application.getInstance().getAppContext().getString(ids[i]);
        }
        return string;
    }

    public static String replaceToken(String input, @StringRes int format, String replacement){
        return input.replace(getString(format), replacement);
    }

    public static String replaceToken(@StringRes int input, @StringRes int format, @StringRes int replacement){
        String inputString = getString(input);
        String formatString = getString(format);
        String replacementString = getString(replacement);
        return inputString.replace(formatString,replacementString);
    }

    public static void underlineTextView(TextView textView){
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static void autosizeTextView(AppCompatTextView textView, int dpMin, int dpMax){
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView,ViewUtil.dpToPx(dpMin),ViewUtil.dpToPx(dpMax),1, TypedValue.COMPLEX_UNIT_PX);
    }


    public static void setLineHeight(TextView textView, int dp)  {
        int lineHeight = dpToPx(dp);
        int fontHeight = textView.getPaint().getFontMetricsInt(null);

        if (lineHeight != fontHeight) {
            textView.setLineSpacing(lineHeight - fontHeight, 1f);
        }
    }

    public static int getStatusBarHeight() {
        if(statusBarHeight==-1){
            statusBarHeight = getStatusBarHeight(Application.getInstance().getAppContext());
        }
        return statusBarHeight;
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavBarHeight() {
        if(navBarHeight==-1){
            navBarHeight = getNavBarHeight(Application.getInstance().getAppContext());
        }
        return navBarHeight;
    }

    private static int getNavBarHeight(Context context) {
        int result = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics realMetrics = new DisplayMetrics();
        display.getRealMetrics(realMetrics);

        int realHeight = realMetrics.heightPixels;
        int realWidth = realMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        boolean hasSoftKeys = (realWidth>displayWidth) || (realHeight>displayHeight);

        // if the device has a navigation bar
        if(hasSoftKeys) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }


}
