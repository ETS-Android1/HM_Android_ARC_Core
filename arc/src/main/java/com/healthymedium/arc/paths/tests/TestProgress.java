package com.healthymedium.arc.paths.tests;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.CircleProgressView;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;

@SuppressLint("ValidFragment")
public class TestProgress extends BaseFragment {

    CircleProgressView circleProgressView;

    TextView textViewHeader;
    String headerText;

    TextView textViewSubHeader;
    String subheaderText;

    TextView textViewThree;
    TextView textViewTextNumber;
    String testNumber;

    int percentageFrom;
    int percentageTo;

    public TestProgress(String header, int index) {
        this.headerText = header;
        this.testNumber = String.valueOf(index+1);
        switch (index){
            case 0:
                percentageFrom = 0;
                percentageTo = 33;
                subheaderText = "Loading next test…";
                break;
            case 1:
                percentageFrom = 33;
                percentageTo = 66;
                subheaderText = "Loading next test…";
                break;
            case 2:
                percentageFrom = 66;
                percentageTo = 100;
                subheaderText = "Well Done!";
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_progress, container, false);

        textViewTextNumber = view.findViewById(R.id.textViewTextNumber);
        textViewTextNumber.setTypeface(Fonts.georgia);
        textViewTextNumber.setText(testNumber);

        textViewThree = view.findViewById(R.id.textViewThree);
        textViewThree.setTypeface(Fonts.georgia);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoBold);
        textViewHeader.setText(headerText);

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setTypeface(Fonts.georgiaItalic);
        textViewSubHeader.setText(subheaderText);

        circleProgressView = view.findViewById(R.id.circleProgressView);
        circleProgressView.setProgress(percentageFrom,false);
        circleProgressView.setProgress(percentageTo,true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Study.openNextFragment();
            }
        },5000);

        return view;
    }

}
