package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.custom.DialogButtonTutorial;
import com.healthymedium.arc.custom.SymbolTutorialButton;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.NavigationManager;

public class SymbolTutorial extends BaseFragment {

    RelativeLayout topSymbols;
    RelativeLayout bottomSymbolsButtons;

    SymbolTutorialButton buttonTop1;
    SymbolTutorialButton buttonTop2;
    SymbolTutorialButton buttonTop3;
    SymbolTutorialButton buttonBottom1;
    SymbolTutorialButton buttonBottom2;

    DialogButtonTutorial centerPopup;

    FrameLayout fullScreenGray;
    FrameLayout progressBarGradient;

    ImageView closeButton;
    ImageView checkmark;

    TextView textView20;
    TextView textViewComplete;

    Button endButton;

    private int shortAnimationDuration;

    public SymbolTutorial() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_symbols_tutorial, container, false);

        topSymbols = view.findViewById(R.id.topSymbols);
        bottomSymbolsButtons = view.findViewById(R.id.bottomSymbolsButtons);

        buttonTop1 = view.findViewById(R.id.symbolbutton_top1);
        buttonTop2 = view.findViewById(R.id.symbolbutton_top2);
        buttonTop3 = view.findViewById(R.id.symbolbutton_top3);

        buttonBottom1 = view.findViewById(R.id.symbolbutton_bottom1);
        buttonBottom2 = view.findViewById(R.id.symbolbutton_bottom2);

        centerPopup = view.findViewById(R.id.centerPopup);

        fullScreenGray = view.findViewById(R.id.fullScreenGray);
        progressBarGradient = view.findViewById(R.id.progressBarGradient);

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        textView20 = view.findViewById(R.id.textView20);
        textViewComplete = view.findViewById(R.id.textViewComplete);

        endButton = view.findViewById(R.id.endButton);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        setInitialImages();

        return view;
    }

    private void setInitialImages() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_8_tutorial, R.drawable.ic_symbol_1_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_7_tutorial, R.drawable.ic_symbol_3_tutorial);

        buttonBottom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeInView(centerPopup, 1f);
                fadeInView(fullScreenGray, 0.9f);
                buttonBottom1.setOnClickListener(null);

                progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 200;

                centerPopup.header.setText("Great job!");
                centerPopup.body.setText("Let's try a couple more for practice.");
                centerPopup.button.setText("Next");

                centerPopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fadeOutView(centerPopup);
                        fadeOutView(fullScreenGray);
                        setSecondImages();
                    }
                });
            }
        });
    }

    private void setSecondImages() {
        buttonTop1.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_6_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        buttonBottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeInView(centerPopup, 1f);
                fadeInView(fullScreenGray, 0.9f);
                buttonBottom2.setOnClickListener(null);

                progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 200;

                centerPopup.header.setText("Nice!");
                centerPopup.body.setText("One more...");
                centerPopup.button.setText("Next");

                centerPopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fadeOutView(centerPopup);
                        fadeOutView(fullScreenGray);
                        setLastImages();
                    }
                });
            }
        });
    }

    private void setLastImages() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        buttonBottom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fadeInView(checkmark, 1f);
                fadeInView(textViewComplete, 1f);
                fadeInView(endButton, 1f);

                fadeOutView(topSymbols);
                fadeOutView(textView20);
                fadeOutView(bottomSymbolsButtons);

                buttonBottom1.setOnClickListener(null);

                progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 200;

                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NavigationManager.getInstance().popBackStack();
                    }
                });

            }
        });
    }

    private void fadeInView(View view, Float opacity) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(opacity)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    private void fadeOutView(final View view) {
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

}
