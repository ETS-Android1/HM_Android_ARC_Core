package com.healthymedium.arc.custom;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.github.gcacace.signaturepad.views.SignaturePad;
import com.healthymedium.arc.library.R;

public class Signature extends FrameLayout {

    public SignaturePad mSignaturePad;
    public TextView clear;

    public Signature(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.custom_signature,this);

        mSignaturePad = findViewById(R.id.signature_pad);
        clear = findViewById(R.id.clear_signature);
    }
}
