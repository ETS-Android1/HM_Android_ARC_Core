package com.healthymedium.arc.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;

public class SymbolTutorialButton extends LinearLayout {

    ImageView topImage;
    ImageView bottomImage;

    public SymbolTutorialButton(Context context) {
        super(context);
        init(context);
    }

    public SymbolTutorialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SymbolTutorialButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_symbol_tutorial_button,this);
        topImage = view.findViewById(R.id.symbolTop);
        bottomImage = view.findViewById(R.id.symbolBottom);
    }


    @Override
    public void setEnabled(boolean enabled) {
        if(enabled){
            setAlpha(1.0f);
        } else {
            setAlpha(0.4f);
        }
        super.setEnabled(enabled);
    }

    public void setSelected(boolean selected){
        if(selected){
            setBackgroundResource(R.drawable.background_symbol_selected);
        } else {
            setBackgroundResource(R.drawable.background_symbol);

        }
    }


    public void setImages(int topId,int bottomId){
        topImage.setImageResource(topId);
        bottomImage.setImageResource(bottomId);
    }

}
