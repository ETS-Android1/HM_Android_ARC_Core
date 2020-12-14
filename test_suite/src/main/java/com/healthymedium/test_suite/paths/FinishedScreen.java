package com.healthymedium.test_suite.paths;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.test_suite.R;
import com.healthymedium.arc.core.BaseFragment;

public class FinishedScreen extends BaseFragment {

    public FinishedScreen() {
        allowBackPress(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.core_fragment_splash, container, false);
        return view;
    }

}
