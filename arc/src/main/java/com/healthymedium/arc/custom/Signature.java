package com.healthymedium.arc.custom;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;

public class Signature extends FrameLayout {

    public Signature(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.custom_signature,this);
    }
}
