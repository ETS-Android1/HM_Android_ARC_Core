package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.Grid2BoxView;
import com.healthymedium.arc.ui.Grid2ChoiceDialog;
import com.healthymedium.arc.ui.base.PointerDrawable;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class Grid2Test extends BaseFragment {

    boolean paused;
    int selectedCount = 0;
    public boolean second = false;

    private int dp16 = ViewUtil.dpToPx(16);
    private int dp60 = ViewUtil.dpToPx(60);
    private int dp160 = ViewUtil.dpToPx(160);

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;
    List<View> selections;

    LinearLayout bottomLinearLayout;
    TextView tapGridText;
    Button button;
    Grid2ChoiceDialog dialog;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            nextFragment();
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
        bottomLinearLayout = view.findViewById(R.id.bottomLinearLayout);
        button = view.findViewById(R.id.buttonContinue);
        tapGridText = view.findViewById(R.id.tapGridText);
        hideButton();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextFragment();
            }
        });

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                int action = event.getAction();
                boolean preventTouch = false;
                Grid2BoxView grid2BoxView = (Grid2BoxView) view;

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        if (view.getTag(R.id.tag_color).equals(R.color.gridSelected)) {
                            view.setTag(R.id.tag_color,R.color.gridNormal);

                            if(selections.contains(view)){
                                view.setTag(R.id.tag_time,0);

                                selections.remove(view);
                                grid2BoxView.removeImage();
                            }
                            selectedCount--;
                        } else if (selectedCount < 3) {
                            selectedCount++;
                            view.setTag(R.id.tag_time, System.currentTimeMillis());
                            view.setTag(R.id.tag_color,R.color.gridSelected);
                            selections.add(view);
                            hideButton();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog = new Grid2ChoiceDialog(getMainActivity(),view, PointerDrawable.POINTER_ABOVE);
                                    dialog.show();

                                    //TODO: listener
                                }
                            },500);

                            //TODO: get item to set
                            grid2BoxView.setImage(R.drawable.phone);
                        } else {
                            preventTouch = true;
                        }

                        handler.removeCallbacks(runnable);
                        if(selectedCount >= 3){
                            showButton();
                            handler.postDelayed(runnable,20000);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return preventTouch;
            }
        };

        // Init each grid-cell as unselected + add onTouchListener
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            gridLayout.getChildAt(i).setTag(R.id.tag_color,R.color.gridNormal);
            gridLayout.getChildAt(i).setOnTouchListener(listener);
        }

        selections = new ArrayList<>();
        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();

        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,20000);

        return view;
    }

    private void nextFragment() {
        handler.removeCallbacks(runnable);
        handlerInteraction.removeCallbacks(runnable);
        if (isVisible()) {
            updateSection();
            Study.openNextFragment();
        }
    }

    private void showButton() {
        button.setVisibility(View.VISIBLE);
        tapGridText.setPadding(0, dp16, 0, 0);
        tapGridText.setHeight(dp60);

        bottomLinearLayout.invalidate();
    }

    private void hideButton() {
        button.setVisibility(View.GONE);
        tapGridText.setPadding(0, 0, 0, 0);
        tapGridText.setMinHeight(LinearLayout.LayoutParams.MATCH_PARENT);

        bottomLinearLayout.invalidate();
    }

    private void updateSection(){
        //TODO
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

}
