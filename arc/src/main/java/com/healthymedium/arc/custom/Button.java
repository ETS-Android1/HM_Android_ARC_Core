package com.healthymedium.arc.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class Button extends FrameLayout {

    TextView textView;
    ImageView imageView;
    View view;
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
        view = inflate(context,R.layout.custom_button,this);
        textView = view.findViewById(R.id.textView);
        imageView = view.findViewById(R.id.imageView);
    }


    private void applyAttributeSet(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Button);
        try {
            textView.setText(a.getString(R.styleable.Button_text));
            inverted = a.getBoolean(R.styleable.Button_inverted,false);
            if(inverted){
                textView.setTextColor(ContextCompat.getColor(getContext(),R.color.primary));
                textView.setBackgroundResource(R.drawable.button_inverted);
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textView.setAlpha(enabled ? 1.0f : 0.5f);
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
