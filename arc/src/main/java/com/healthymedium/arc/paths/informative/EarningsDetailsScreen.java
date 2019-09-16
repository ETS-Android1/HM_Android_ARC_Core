package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.earnings.EarningsDetailedCycleView;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsDetailsScreen extends BaseFragment {

    SwipeRefreshLayout refreshLayout;

    Button viewFaqButton;
    TextView studyTotal;
    TextView lastSync;
    LinearLayout cycleLayout;

    public EarningsDetailsScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_details, container, false);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!Study.getRestClient().isUploadQueueEmpty()){
                    refreshLayout.setRefreshing(false);
                    return;
                }

                Study.getParticipant().getEarnings().refreshDetails(new Earnings.Listener() {
                    @Override
                    public void onSuccess() {
                        if(refreshLayout!=null) {
                            refreshLayout.setRefreshing(false);
                            Study.getParticipant().save();
                            if(getContext()!=null) {
                                populateViews();
                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        if(refreshLayout!=null) {
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        });

        TextView textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        viewFaqButton = view.findViewById(R.id.viewFaqButton);
        viewFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });

        studyTotal = view.findViewById(R.id.studyTotal);
        lastSync = view.findViewById(R.id.textViewLastSync);
        cycleLayout = view.findViewById(R.id.cycleLayout);

        populateViews();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
    }

    private void populateViews() {

        Earnings earnings = Study.getParticipant().getEarnings();
        EarningDetails details = earnings.getDetails();

        if(details==null){
            return;
        }

        studyTotal.setText(details.total_earnings);

        String syncString = getString(R.string.earnings_sync) + " ";
        DateTime lastSyncTime = earnings.getDetailsRefreshTime();
        if(lastSyncTime != null) {
            if(lastSyncTime.plusMinutes(1).isBeforeNow()) {
                String date = JodaUtil.format(lastSyncTime, R.string.format_date, Application.getInstance().getLocale());
                String time = JodaUtil.format(lastSyncTime, R.string.format_time, Application.getInstance().getLocale());
                String dateTime = getString(R.string.earnings_sync_datetime);

                dateTime = ViewUtil.replaceToken(dateTime,R.string.token_date,date);
                dateTime = ViewUtil.replaceToken(dateTime,R.string.token_time,time);

                syncString += dateTime;
            } else {
                syncString += getString(R.string.earnings_sync_justnow);
            }
        }
        lastSync.setText(syncString);

        cycleLayout.removeAllViews();
        for(EarningDetails.Cycle cycle : details.cycles){
            EarningsDetailedCycleView cycleView = new EarningsDetailedCycleView(getContext(),cycle);
            cycleLayout.addView(cycleView);
        }
    }

}
