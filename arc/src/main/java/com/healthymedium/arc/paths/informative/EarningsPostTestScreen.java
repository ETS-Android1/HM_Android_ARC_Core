package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.earnings.EarningsGoalView;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsPostTestScreen extends BaseFragment {

    TextView weeklyTotal;
    TextView studyTotal;
    LinearLayout goalLayout;

    public EarningsPostTestScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_post_test, container, false);

        goalLayout = view.findViewById(R.id.goalLayout);

        EarningOverview overview = Study.getParticipant().getEarnings().getOverview();
        if(overview==null){
            overview = EarningOverview.getTestObject();
        }

        weeklyTotal = view.findViewById(R.id.weeklyTotal);
        weeklyTotal.setText(overview.cycle_earnings);

        studyTotal = view.findViewById(R.id.studyTotal);
        studyTotal.setText(overview.total_earnings);

        for(EarningOverview.Goals.Goal goal : overview.goals.getList()){
            goalLayout.addView(new EarningsGoalView(getContext(),goal, overview.cycle));
        }

        Button button = view.findViewById(R.id.buttonNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Study.openNextFragment();
            }
        });

        return view;
    }

}
