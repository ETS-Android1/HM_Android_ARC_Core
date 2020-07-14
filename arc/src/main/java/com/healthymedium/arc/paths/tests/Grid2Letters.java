package com.healthymedium.arc.paths.tests;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.GridTestPathData;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2Letters extends BaseFragment {

    boolean paused;
    GridLayout gridLayout;
    protected GridTestPathData gridTest;
    protected GridTestPathData.Section section;
    protected int eCount = 0;
    protected int fCount = 0;

    private TextView textViewTapFsLabel;

    public Grid2Letters() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_letters, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        textViewTapFsLabel = view.findViewById(R.id.tapGridText);
        textViewTapFsLabel.setText(ViewUtil.getHtmlString(R.string.grids_subheader_fs));

        //TODO: listener

        Typeface font = Fonts.georgia;
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            ((TextView)gridLayout.getChildAt(i)).setTypeface(font);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
