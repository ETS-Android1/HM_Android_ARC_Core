package com.healthymedium.arc.paths.tests;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.ViewUtil;

public class TestIntro extends BaseFragment {

    TextView header;
    TextView subheader;
    Button nextButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tests_intro, container, false);

        header = view.findViewById(R.id.header);
        header.setText(ViewUtil.getHtmlString(R.string.testing_intro_header));

        subheader = view.findViewById(R.id.subheader);
        subheader.setText(ViewUtil.getHtmlString(R.string.testing_intro_body));

        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setText(ViewUtil.getString(R.string.button_next));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openNextFragment();
            }
        });

        return view;
    }
}
