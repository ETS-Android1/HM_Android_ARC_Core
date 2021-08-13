package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.earnings.EarningsDetailedCycleView;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsDetailsScreen extends BaseFragment {

    SwipeRefreshLayout refreshLayout;

    Button viewFaqButton;
    TextView studyTotal;
    TextView lastSync;
    LinearLayout cycleLayout;
    TextView textViewBack;

    public EarningsDetailsScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_details, container, false);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
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

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getHtmlString(R.string.button_back));

        if(Study.getParticipant().getEarnings().hasCurrentDetails()){
            populateViews();
        } else {
            refresh();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
        refreshLayout.setProgressViewOffset(true, 0,top+ViewUtil.dpToPx(16));
    }

    private void refresh() {
        if (!Study.getRestClient().isUploadQueueEmpty()){
            refreshLayout.setRefreshing(false);
            if(getContext()!=null) {
                populateViews();
            }
            return;
        }

        refreshLayout.setRefreshing(true);

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
                    if(getContext()!=null) {
                        populateViews();
                    }
                }
            }
        });
    }

    private void populateViews() {

        Earnings earnings = Study.getParticipant().getEarnings();
        EarningDetails details = earnings.getDetails();

        if(details==null){
            return;
        }

        studyTotal.setText(details.total_earnings);
        lastSync.setVisibility(View.GONE);

        cycleLayout.removeAllViews();
        for(EarningDetails.Cycle cycle : details.cycles){
            EarningsDetailedCycleView cycleView = new EarningsDetailedCycleView(getContext(),cycle);
            cycleLayout.addView(cycleView);
        }
    }

}
