package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;

public class SymbolView extends RoundedLinearLayout {

    ImageView topImage;
    ImageView bottomImage;

    public SymbolView(Context context) {
        super(context);
        init(context);
    }

    public SymbolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SymbolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_symbol_view,this);
        topImage = view.findViewById(R.id.symbolTop);
        bottomImage = view.findViewById(R.id.symbolBottom);
    }

    public void setImages(int topId,int bottomId){
        topImage.setImageResource(topId);
        bottomImage.setImageResource(bottomId);
    }

}
