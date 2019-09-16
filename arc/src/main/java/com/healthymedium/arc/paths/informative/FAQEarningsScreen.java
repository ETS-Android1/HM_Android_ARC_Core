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

public class FAQEarningsScreen extends BaseFragment {

    TextView textViewBack;

    RelativeLayout earnings_q1;
    RelativeLayout earnings_q2;
    RelativeLayout earnings_q3;
    RelativeLayout earnings_q4;
    RelativeLayout earnings_q5;

    public FAQEarningsScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq_earnings, container, false);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });
        textViewBack.setVisibility(View.VISIBLE);

        earnings_q1 = view.findViewById(R.id.earnings_q1);
        earnings_q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_earnings_q1), ViewUtil.getString(R.string.faq_earnings_a1));
                NavigationManager.getInstance().open(fragment);
            }
        });

        earnings_q2 = view.findViewById(R.id.earnings_q2);
        earnings_q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_earnings_q2), ViewUtil.getString(R.string.faq_earnings_a2));
                NavigationManager.getInstance().open(fragment);
            }
        });

        earnings_q3 = view.findViewById(R.id.earnings_q3);
        earnings_q3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_earnings_q3), ViewUtil.getString(R.string.faq_earnings_a3));
                NavigationManager.getInstance().open(fragment);
            }
        });

        earnings_q4 = view.findViewById(R.id.earnings_q4);
        earnings_q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_earnings_q4), ViewUtil.getString(R.string.faq_earnings_a4));
                NavigationManager.getInstance().open(fragment);
            }
        });

        earnings_q5 = view.findViewById(R.id.earnings_q5);
        earnings_q5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_earnings_q5), ViewUtil.getString(R.string.faq_earnings_a5));
                NavigationManager.getInstance().open(fragment);
            }
        });

        return view;
    }
}
