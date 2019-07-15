package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

public class TestBegin extends BaseFragment {

    TextView begin;
    TextView number;

    int count = 3;

    Handler handler;
    Runnable runnableCountdown = new Runnable() {
        @Override
        public void run() {
            count--;
            if (count >= 1) {
                number.animate()
                        .alpha(0)
                        .setDuration(100)
                        .withEndAction(runnableDisappear);
            } else {
                Study.openNextFragment();
            }
        }
    };

    Runnable runnableDisappear = new Runnable() {
        @Override
        public void run() {
            number.setAlpha(0);
            number.setText(String.valueOf(count));
            number.animate()
                    .alpha(1)
                    .setDuration(100)
                    .withEndAction(runnableReappear);
        }
    };

    Runnable runnableReappear = new Runnable() {
        @Override
        public void run() {
            number.setAlpha(1);
            handler.postDelayed(runnableCountdown,800);
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

        begin = view.findViewById(R.id.header);
        begin.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_begin)));

        number = view.findViewById(R.id.number);

        handler = new Handler();
        handler.postDelayed(runnableCountdown,900);

        return view;
    }
}
