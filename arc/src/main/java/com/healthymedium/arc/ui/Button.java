package com.healthymedium.arc.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthymedium.arc.ui.base.ChipButton;
import com.healthymedium.arc.ui.base.SimpleGradient;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class Button extends ChipButton {

    public static final int THEME_PRIMARY = 0;
    public static final int THEME_WHITE = 1;
    public static final int THEME_BLACK = 2;

    TextView textView;
    ImageView imageView;

    public Button(Context context) {
        super(context);
        init(context);
        applyAttributeSet(context,null);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        applyAttributeSet(context, attrs);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        applyAttributeSet(context, attrs);
    }

    private void init(Context context){

        textView = new TextView(context);
        textView.setTypeface(Fonts.robotoBold);
        textView.setTextSize(18);

        imageView = new ImageView(context);
        imageView.setVisibility(GONE);

        addView(imageView);
        addView(textView);

        setElevation(ViewUtil.dpToPx(2));
    }

    private void applyAttributeSet(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Button);
        try {
            int normalTop = R.color.primaryButtonLight;
            int normalBottom = R.color.primaryButtonDark;
            int selected = R.color.primaryButtonDark;
            int textColor = R.color.white;

            textView.setText(a.getString(R.styleable.Button_android_text));
            if(a.hasValue(R.styleable.Button_buttonTheme)){
                int styleEnum = a.getInt(R.styleable.Button_buttonTheme,0);
                switch (styleEnum){
                    case THEME_PRIMARY:
                        break; // already set
                    case THEME_WHITE:
                        normalTop = R.color.whiteButtonLight;
                        normalBottom = R.color.whiteButtonDark;
                        selected = R.color.whiteButtonSelected;
                        textColor = R.color.black;
                        break;
                    case THEME_BLACK:
                        normalTop = R.color.blackButtonLight;
                        normalBottom = R.color.blackButtonDark;
                        selected = R.color.blackButtonSelected;
                        break;
                }
            }

            topLayer.setStrokeGradient(SimpleGradient.LINEAR_VERTICAL, ViewUtil.getColor(context,normalTop), ViewUtil.getColor(context,normalBottom));
            topLayer.setFillGradient(SimpleGradient.LINEAR_VERTICAL, ViewUtil.getColor(context,normalTop), ViewUtil.getColor(context,normalBottom));
            bottomLayer.setFillColor(ViewUtil.getColor(context,selected));
            textView.setTextColor(ViewUtil.getColor(context,textColor));

            setIcon(a.getDrawable(R.styleable.Button_icon));
            boolean enabled = a.getBoolean(R.styleable.Button_android_enabled,true);
            setEnabled(enabled);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public void clearText() {
        textView.setText("");
    }

    public void setText(String string){
        imageView.setVisibility(GONE);
        textView.setText(string);
    }

    public void setIcon(@DrawableRes int id) {
        setIcon(ViewUtil.getDrawable(id));
    }

    public void setIcon(Drawable drawable){
        if(drawable!=null) {
            clearText();
            imageView.setBackground(drawable);
            imageView.setVisibility(VISIBLE);
        }
    }
}
