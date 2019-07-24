package com.healthymedium.arc.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthymedium.arc.custom.base.ChipButton;
import com.healthymedium.arc.custom.base.SimpleGradient;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class Button extends ChipButton {

    TextView textView;
    ImageView imageView;
    boolean inverted;

    public Button(Context context) {
        super(context);
        init(context);
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
        textView.setTextColor(ViewUtil.getColor(context,R.color.white));
        textView.setTypeface(Fonts.robotoBold);
        textView.setTextSize(18);

        imageView = new ImageView(context);
        imageView.setVisibility(GONE);

        int colorTop = ViewUtil.getColor(context,R.color.primaryButtonLight);
        int colorBottom = ViewUtil.getColor(context,R.color.primaryButtonDark);

        topLayer.setStrokeGradient(SimpleGradient.LINEAR_VERTICAL, colorTop, colorBottom);
        topLayer.setFillGradient(SimpleGradient.LINEAR_VERTICAL, colorTop, colorBottom);
        bottomLayer.setFillColor(ViewUtil.getColor(R.color.primaryButtonDark));

        addView(imageView);
        addView(textView);
    }

    private void applyAttributeSet(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Button);
        try {
            textView.setText(a.getString(R.styleable.Button_text));
            inverted = a.getBoolean(R.styleable.Button_inverted,false);
            if(inverted){
                textView.setTextColor(ViewUtil.getColor(R.color.black));

                int colorTop = ViewUtil.getColor(context,R.color.whiteButtonLight);
                int colorBottom = ViewUtil.getColor(context,R.color.whiteButtonDark);

                topLayer.setStrokeGradient(SimpleGradient.LINEAR_VERTICAL, colorTop, colorBottom);
                topLayer.setFillGradient(SimpleGradient.LINEAR_VERTICAL, colorTop, colorBottom);
                bottomLayer.setFillColor(ViewUtil.getColor(R.color.whiteButtonSelected));

            }
            setIcon(a.getDrawable(R.styleable.Button_icon));
            boolean enabled = a.getBoolean(R.styleable.Button_enabled,true);
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
