package com.healthymedium.arc.ui;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.tests.Grid2Test;
import com.healthymedium.arc.ui.base.PointerDialog;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2ChoiceDialog extends PointerDialog {

    Grid2ChoiceView phone;
    boolean phoneEnabled;

    Grid2ChoiceView key;
    boolean keyEnabled;

    Grid2ChoiceView pen;
    boolean penEnabled;

    TextView textViewGridDialog;
    Listener listener;

    public Grid2ChoiceDialog(Activity activity, View target, int pointerConfig, boolean phoneEnabled, boolean keyEnabled, boolean penEnabled) {
        super(activity, target, null, pointerConfig);
        this.phoneEnabled = phoneEnabled;
        this.keyEnabled = keyEnabled;
        this.penEnabled = penEnabled;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_grid2_choice,null);

        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }
                if(listener==null){
                    return false;
                }
                int id = ((Grid2ChoiceView)v).getDrawableImageId();
                listener.onSelected(id);
                dismiss();
                return false;
            }
        };

        phone = view.findViewById(R.id.phone);
        phone.setImage(R.drawable.phone);
        if(phoneEnabled) {
            phone.setOnTouchListener(touchListener);
        } else {
            phone.setAlpha(0.4f);
        }

        key = view.findViewById(R.id.key);
        key.setImage(R.drawable.key);
        if(keyEnabled) {
            key.setOnTouchListener(touchListener);
        } else {
            key.setAlpha(0.4f);
        }

        pen = view.findViewById(R.id.pen);
        pen.setImage(R.drawable.pen);
        if(penEnabled) {
            pen.setOnTouchListener(touchListener);
        } else {
            pen.setAlpha(0.4f);
        }

        textViewGridDialog = view.findViewById(R.id.textViewGridDialog);
        textViewGridDialog.setText("Select the item that was here");

        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setElevation(ViewUtil.dpToPx(4));
        setRadius(16);
        setView(view);

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSelected(@DrawableRes int image);
    }

}
