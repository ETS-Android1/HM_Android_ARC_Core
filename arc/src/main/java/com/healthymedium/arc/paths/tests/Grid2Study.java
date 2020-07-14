package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.TimedDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2Study extends BaseFragment {

    boolean paused;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;

    TimedDialog dialog;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isVisible()){
                Study.openNextFragment();
            }
        }
    };

    public Grid2Study() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_study, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);

        dialog = new TimedDialog(ViewUtil.getHtmlString(R.string.grids_overlay1),2000);
        dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
            @Override
            public void dismiss() {
                setupTest();
                handler = new Handler();
                handler.postDelayed(runnable,3000);
            }
        });
        dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());

        gridTest = (GridTestPathData) Study.getCurrentSegmentData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!gridTest.hasStarted()){
            gridTest.markStarted();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!paused) {
            gridTest.startNewSection();
            section = gridTest.getCurrentSection();
        } else {
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog.isVisible()){
            dialog.setOnDialogDismissListener(null);
            dialog.dismiss();
        }
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
        paused = true;
    }

    private void setupTest(){
        //TODO
    }
}
