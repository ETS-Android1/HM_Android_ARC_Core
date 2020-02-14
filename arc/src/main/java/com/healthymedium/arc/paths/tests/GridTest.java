package com.healthymedium.arc.paths.tests;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.Log;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class GridTest extends BaseFragment {

    boolean paused;
    int selectedCount = 0;
    public boolean second = false;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;
    List<View> selections;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            handlerInteraction.removeCallbacks(runnable);
            if(isVisible()){
                updateSection();
                Study.openNextFragment();
            }
        }
    };

    public GridTest() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_test, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        setGridCellDimens(60, 105); //"matches" invision comp

        // Select/unselect grid-cell onTouchListener object
        View.OnTouchListener selectionListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        // If view not selected and fewer than 3 already selected...
                        if(!selections.contains(v) && selectedCount < 3) {
                            // Set the selected view to visible
                            v.findViewById(R.id.cellSelectedForeground).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.cellSelectedBackground).setVisibility(View.VISIBLE);

                            // Tag as selected
                            v.setTag(R.id.tag_color, R.color.gridSelected);
                            v.setTag(R.id.tag_time, System.currentTimeMillis());

                            // Add the touched view to selections
                            selections.add(v);
                            ++selectedCount;

                            // Output grid-cell statuses to logcat
                            //logGrid();
                        }

                        // If view is already selected...
                        else if(selections.contains(v)) {
                            // Hide the selected views
                            v.findViewById(R.id.cellSelectedForeground).setVisibility(View.INVISIBLE);
                            v.findViewById(R.id.cellSelectedBackground).setVisibility(View.INVISIBLE);

                            // Tag as unselected
                            v.setTag(R.id.tag_color, R.color.gridNormal);
                            v.setTag(R.id.tag_time, 0);

                            // Remove from selections
                            selections.remove(v);
                            --selectedCount;

                            // Output grid-cell statuses to logcat
                            //logGrid();
                        }

                    break;

                    // Do nothing
                    case MotionEvent.ACTION_UP:
                    break;
                }

                handler.removeCallbacks(runnable);
                if(selectedCount == 3)
                    handler.postDelayed(runnable, 2000);

                return false;
            }
        };

        // Init each grid-cell as unselected + add onTouchlistener
        for(int i = 0; i < gridLayout.getChildCount(); ++i) {
            gridLayout.getChildAt(i).setTag(R.id.tag_color, R.color.gridNormal);
            gridLayout.getChildAt(i).setOnTouchListener(selectionListener);
        }

        selections = new ArrayList<>();
        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();


        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,20000);

        return view;
    }

    private ImageView getView(int row, int col){
        return (ImageView)gridLayout.getChildAt((5*row)+col);
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

    private void updateSection(){
        int size = gridLayout.getChildCount();
        List<GridTestPathData.Tap> choices = new ArrayList<>();
        for(int i=0;i<size;i++){
            if(selections.contains(gridLayout.getChildAt(i))){
                View view = gridLayout.getChildAt(i);
                choices.add(new GridTestPathData.Tap(i / 5,i % 5,(long)view.getTag(R.id.tag_time)));
            }
        }
        section.setChoices(choices);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
    }

    private void setGridCellDimens(int width_dp, int height_dp) {
        if(gridLayout == null || gridLayout.getChildCount() < 1) {
            Log.e("GridTest", "(GridLayout) gridLayout has 0 children or is null");
            return;
        }

        int width_px = ViewUtil.dpToPx(width_dp);
        int height_px = ViewUtil.dpToPx(height_dp);

        View cellFrame, cellUnselected, cellSelectedBG, cellSelectedFG;
        for(int i = 0; i < gridLayout.getChildCount(); ++i) {
            cellFrame = gridLayout.getChildAt(i);
            cellUnselected = cellFrame.findViewById(R.id.cellUnselectedView);
            cellSelectedBG = cellFrame.findViewById(R.id.cellSelectedBackground);
            cellSelectedFG = cellFrame.findViewById(R.id.cellSelectedForeground);

            //Adjust the cells FrameLayout params
            cellFrame.getLayoutParams().width = width_px;
            cellFrame.getLayoutParams().height = height_px;

            cellUnselected.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            cellUnselected.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

            cellSelectedBG.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            cellSelectedBG.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

            cellSelectedFG.getLayoutParams().width = width_px / 10 * 4;
            cellSelectedFG.getLayoutParams().height = width_px / 10 * 4;
        }
    }

    private void logGrid() {
        if(gridLayout == null || gridLayout.getChildCount() < 1) {
            Log.e("GridTest", "(GridLayout) gridLayout has 0 children or is null");
            return;
        }

        //Print every child and their selection status
        for(int i = 0; i < gridLayout.getChildCount(); ++i) {
            String isSelected = "";
            if(gridLayout.getChildAt(i).getTag(R.id.tag_color).equals(R.color.gridSelected)) {
                isSelected = "(selected)";
            }
            Log.d("GridTest", String.format("\t#%2d\t%s", i, isSelected));
        }

        Log.d("GridTest", "\n-----");
    }

}
