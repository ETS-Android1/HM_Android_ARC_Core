package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.ui.BottomNavigationView;
import com.healthymedium.arc.ui.FaqListItem;
import com.healthymedium.arc.utilities.ViewUtil;

public class FAQScreen extends BaseFragment {

    TextView textViewBack;
    TextView header;

    FaqListItem testing;
    FaqListItem earnings;
    FaqListItem technology;


    public FAQScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });
        textViewBack.setVisibility(View.VISIBLE);

        header = view.findViewById(R.id.textViewHeader);
        header.setTypeface(Fonts.robotoMedium);

        testing = view.findViewById(R.id.testing);
        testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQTestingScreen();
                NavigationManager.getInstance().open(fragment);
            }
        });

        earnings = view.findViewById(R.id.earnings);
        if (BottomNavigationView.shouldShowEarnings) {
            earnings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseFragment fragment = new FAQEarningsScreen();
                    NavigationManager.getInstance().open(fragment);
                }
            });
        } else {
            earnings.setVisibility(View.GONE);
        }

        technology = view.findViewById(R.id.technology);
        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQTechnologyScreen();
                NavigationManager.getInstance().open(fragment);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setPadding(0, ViewUtil.getStatusBarHeight(),0,0);
    }

}
