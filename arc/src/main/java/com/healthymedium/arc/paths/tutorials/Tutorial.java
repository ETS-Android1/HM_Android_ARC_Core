package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.utilities.NavigationManager;

public class Tutorial extends BaseFragment {

    protected static final String HINT_FIRST_TUTORIAL = "HINT_FIRST_TUTORIAL";

    protected int shortAnimationDuration;

    protected View loadingView;
    protected LinearLayout progressBar;

    protected ImageView checkmark;
    protected TextView textViewComplete;
    protected Button endButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    public Tutorial() {
    }

    protected void fadeInView(View view, Float opacity) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(opacity)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    protected void fadeOutView(final View view) {
        view.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    // Displays the tutorial complete screen
    protected void showComplete() {
        fadeInView(checkmark, 1f);
        fadeInView(textViewComplete, 1f);
        fadeInView(endButton, 1f);

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }

    protected void exit(){
        loadingView.animate()
                .setDuration(400)
                .translationY(0);
        progressBar.animate()
                .setDuration(400)
                .alpha(0.0f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NavigationManager.getInstance().popBackStack();
            }
        },1200);
    }
}
