package com.healthymedium.arc.paths.informative;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.earnings.EarningsGoalView;
import com.healthymedium.arc.utilities.NavigationManager;

import java.util.Random;

public class EarningsPostTestLoadingScreen extends BaseFragment {

    ProgressBar progressBar;
    TextView textView;

    Earnings earnings;
    Handler handler;

    public EarningsPostTestLoadingScreen() {
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_post_test_loading, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.textView);
        handler = new Handler();
        handler.postDelayed(runnable,10000);

        earnings = Study.getParticipant().getEarnings();
        earnings.refreshOverview(new Earnings.Listener() {
            @Override
            public void onSuccess() {
                checkLoading();
            }

            @Override
            public void onFailure() {
                checkLoading();
            }
        });
        earnings.refreshDetails(new Earnings.Listener() {
            @Override
            public void onSuccess() {
                checkLoading();
            }

            @Override
            public void onFailure() {
                checkLoading();
            }
        });

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        animateAlpha(1.0f);
    }

    @Override
    protected void onExitTransitionStart(boolean popped) {
        super.onEnterTransitionEnd(popped);
        animateAlpha(0f);
    }

    private void animateAlpha(float value) {
        if(textView!=null){
            textView.animate().alpha(value);
        }
        if(progressBar!=null) {
            progressBar.animate().alpha(value);
        }
    }

    private void checkLoading() {
        if(EarningsPostTestLoadingScreen.this==null){
            return;
        }
        if(earnings.isRefreshingOverview()||earnings.isRefreshingDetails()) {
            return;
        }
        handler.removeCallbacks(runnable);
        if(earnings.hasCurrentOverview() && earnings.hasCurrentDetails()) {
            openSuccess();
        } else {
            openFailure();
        }
    }

    private void openSuccess(){
        NavigationManager.getInstance().open(new EarningsPostTestScreen());
    }

    private void openFailure(){
        NavigationManager.getInstance().open(new EarningsPostTestUnavailableScreen());
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(Config.REST_BLACKHOLE){
                openSuccess();
//                openFailure();
                return;
            }
            openFailure();
        }
    };

}
