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

public class EarningsScreen extends BaseFragment {

    TextView weeklyTotal;
    TextView studyTotal;
    TextView lastSync;
    LinearLayout goalLayout;

    TextView earningsBody1;
    Button viewDetailsButton;
    TextView bonusBody;
    Button viewFaqButton;

    public EarningsScreen() {
        allowBackPress(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        earningsBody1 = view.findViewById(R.id.earningsBody1);
        earningsBody1.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_body1)));

        viewDetailsButton = view.findViewById(R.id.viewDetailsButton);
        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EarningsDetailsScreen earningsDetailsScreen = new EarningsDetailsScreen();
                NavigationManager.getInstance().open(earningsDetailsScreen);
            }
        });

        bonusBody = view.findViewById(R.id.bonusBody);
        bonusBody.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_body)));

        viewFaqButton = view.findViewById(R.id.viewFaqButton);
        viewFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });

        goalLayout = view.findViewById(R.id.goalLayout);

        EarningOverview overview = Study.getParticipant().getEarnings().getOverview();
        if(overview==null){
            overview = EarningOverview.getTestObject();
        }

        weeklyTotal = view.findViewById(R.id.weeklyTotal);
        weeklyTotal.setText(overview.cycle_earnings);

        studyTotal = view.findViewById(R.id.studyTotal);
        studyTotal.setText(overview.total_earnings);

        String syncString = getString(R.string.earnings_sync) + " ";
        DateTime lastSyncTime = Study.getParticipant().getEarnings().getOverviewRefreshTime();
        if(lastSyncTime != null) {
            if(lastSyncTime.plusMinutes(1).isAfterNow()) {
                syncString += JodaUtil.format(
                        lastSyncTime,
                        R.string.format_date,
                        Application.getInstance().getLocale());
            } else {
                syncString += getString(R.string.earnings_sync_justnow);
            }
        }
        lastSync = view.findViewById(R.id.textViewLastSync);
        lastSync.setText(syncString);

        for(EarningOverview.Goals.Goal goal : overview.goals.getList()){
            goalLayout.addView(new EarningsGoalView(getContext(),goal));
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
        getMainActivity().showNavigationBar();
    }
}
