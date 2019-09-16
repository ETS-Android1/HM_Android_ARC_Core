package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class FAQTechnologyScreen extends BaseFragment {

    TextView textViewBack;

    RelativeLayout tech_q1;
    RelativeLayout tech_q2;
    RelativeLayout tech_q3;
    RelativeLayout tech_q4;

    public FAQTechnologyScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq_technology, container, false);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });
        textViewBack.setVisibility(View.VISIBLE);

        tech_q1 = view.findViewById(R.id.tech_q1);
        tech_q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_tech_q1), ViewUtil.getString(R.string.faq_tech_a1));
                NavigationManager.getInstance().open(fragment);
            }
        });

        tech_q2 = view.findViewById(R.id.tech_q2);
        tech_q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_tech_q2), ViewUtil.getString(R.string.faq_tech_a2));
                NavigationManager.getInstance().open(fragment);
            }
        });

        tech_q3 = view.findViewById(R.id.tech_q3);
        tech_q3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_tech_q3), ViewUtil.getString(R.string.faq_tech_a3));
                NavigationManager.getInstance().open(fragment);
            }
        });

        tech_q4 = view.findViewById(R.id.tech_q4);
        tech_q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_tech_q4), ViewUtil.getString(R.string.faq_tech_a4));
                NavigationManager.getInstance().open(fragment);
            }
        });

        return view;
    }
}
