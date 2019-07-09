package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;

public class TestBegin extends BaseFragment {

    TextView number;

    int count = 3;

    Handler handler;
    Runnable runnableCountdown = new Runnable() {
        @Override
        public void run() {
            count--;
            if (count >= 1) {
                number.setText(String.valueOf(count));
                handler.postDelayed(runnableCountdown,1000);
            } else {
                Study.openNextFragment();
            }
        }
    };

    public TestBegin() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_begin, container, false);

        number = view.findViewById(R.id.number);

        handler = new Handler();
        handler.postDelayed(runnableCountdown,1000);

        return view;
    }
}
