package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.Grid2BoxView;
import com.healthymedium.arc.ui.Grid2ChoiceDialog;
import com.healthymedium.arc.ui.base.PointerDrawable;

import java.util.ArrayList;
import java.util.List;

public class Grid2Test extends BaseFragment {

    boolean paused;

    boolean phoneSelected = false;
    boolean keySelected = false;
    boolean penSelected = false;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;

    Button button;
    Grid2ChoiceDialog dialog;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            openNextFragment();
        }
    };

    public Grid2Test() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_test, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);

        Grid2BoxView.Listener listener = new Grid2BoxView.Listener() {
            @Override
            public boolean onSelected(final Grid2BoxView view, boolean selected) {
                handler.removeCallbacks(runnable);

                if(selected){
                    disableGrids(view);
                    dialog = new Grid2ChoiceDialog(
                            getMainActivity(),
                            view,
                            PointerDrawable.POINTER_BELOW,
                            !phoneSelected,
                            !keySelected,
                            !penSelected);
                    dialog.setAnimationDuration(50);
                    dialog.setListener(new Grid2ChoiceDialog.Listener() {
                        @Override
                        public void onSelected(int image) {
                            view.setImage(image);
                            updateSelections();
                            if(phoneSelected && keySelected && penSelected) {
                                handler.postDelayed(runnable,2000);
                                button.setVisibility(View.VISIBLE);
                                enableGridsFinal();
                            } else {
                                button.setVisibility(View.GONE);
                                enableGrids();
                            }
                        }
                    });
                    dialog.show();
                } else {
                    view.removeImage();
                    updateSelections();
                    enableGrids();
                    dialog.dismiss();
                }
                return false;
            }
        };

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView grid2BoxView = (Grid2BoxView) gridLayout.getChildAt(i);
            grid2BoxView.setListener(listener);
        }

        button = view.findViewById(R.id.buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNextFragment();
            }
        });

        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();

        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,20000);

        return view;
    }

    private void openNextFragment() {
        handler.removeCallbacks(runnable);
        handlerInteraction.removeCallbacks(runnable);
        if (isVisible()) {
            updateSection();
            Study.openNextFragment();
        }
    }

    private void updateSection(){
        List<GridTestPathData.Tap> choices = new ArrayList<>();

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(view.isSelected()) {
                // TODO: Make sure we collect the data we need for this test version
                choices.add(new GridTestPathData.Tap(i / 5, i % 5, view.getTimestampImage()));
            }
        }

        section.setChoices(choices);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
    }

    @Override
    public void onStart() {
        super.onStart();
        section.markTestGridDisplayed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            updateSection();
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerInteraction.removeCallbacks(runnable);
        handler.removeCallbacks(runnable);
        paused = true;
    }


    private void disableGrids(Grid2BoxView exemption){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(false);
        }
        if(exemption!=null) {
            exemption.setSelectable(true);
        }
    }

    private void enableGridsFinal(){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(view.isSelected()) {
                view.setSelectable(true);
            } else {
                view.setSelectable(false);
            }
        }
    }

    private void enableGrids(){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(true);
        }
    }

    private void updateSelections(){
        phoneSelected = false;
        keySelected = false;
        penSelected = false;

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(!view.isSelected()){
                continue;
            }
            int id = view.getImage();
            if(id == R.drawable.phone) {
                phoneSelected = true;
            }
            if(id == R.drawable.key) {
                keySelected = true;
            }
            if(id == R.drawable.pen) {
                penSelected = true;
            }
        }
    }

}
