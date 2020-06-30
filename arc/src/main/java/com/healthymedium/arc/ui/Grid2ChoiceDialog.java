package com.healthymedium.arc.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.PointerDialog;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2ChoiceDialog extends PointerDialog {


    public Grid2ChoiceDialog(Activity activity, View target, int pointerConfig) {
        super(activity, target, null, pointerConfig);
        init();
    }

    public Grid2ChoiceDialog(Activity activity, View target) {
        super(activity, target, null);
        init();
    }

    private void init() {
        // todo: actually inflate view
        // View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_grid2_choice,null);




        setElevation(ViewUtil.dpToPx(4));
        setRadius(16);
        //setView(view);
    }

}
