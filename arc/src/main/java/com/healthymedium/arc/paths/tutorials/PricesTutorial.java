package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.DialogButtonTutorial;
import com.healthymedium.arc.custom.RadioButton;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class PricesTutorial extends BaseFragment {

    RadioButton buttonYes;
    RadioButton buttonNo;

    TextView textviewFood;
    TextView textviewPrice;

    DialogButtonTutorial bottomPopup;
    DialogButtonTutorial centerPopup;

    FrameLayout fullScreenGray;
    FrameLayout progressBarGradient;

    ImageView closeButton;

    private int shortAnimationDuration;

    public PricesTutorial() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prices_tutorial, container, false);

        buttonYes = view.findViewById(R.id.radioButtonYes);
        buttonYes.setText(ViewUtil.getString(R.string.YES));
        buttonYes.setCheckable(false);

        buttonNo = view.findViewById(R.id.radioButtonNo);
        buttonNo.setText(ViewUtil.getString(R.string.NO));
        buttonNo.setCheckable(false);

        bottomPopup = view.findViewById(R.id.bottomPopup);
        centerPopup = view.findViewById(R.id.centerPopup);

        fullScreenGray = view.findViewById(R.id.fullScreenGray);
        progressBarGradient = view.findViewById(R.id.progressBarGradient);

        closeButton = view.findViewById(R.id.closeButton);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        // TODO
        // This is the wrong  type of popup
        fadeInView(bottomPopup, 1f);
        bottomPopup.header.setText("What do you think?");
        bottomPopup.body.setText("Choose the answer that makes sense to you.");
        bottomPopup.button.setText("Next");

        setFirstPricesCompare();

        return view;
    }

    private void setFirstPricesCompare() {
        textviewFood.setText("Bananas");
        textviewPrice.setText("$3.27");

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        fadeInView(bottomPopup, 1f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        bottomPopup.header.setText("Great choice!");
                        bottomPopup.body.setText("Let's try another.");
                        bottomPopup.button.setText("Next");

                        bottomPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(bottomPopup);
                                buttonYes.setChecked(false);
                                setSecondPricesCompare();
                            }
                        });

                        break;
                }
                return true;
            }
        });

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        fadeInView(bottomPopup, 1f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        bottomPopup.header.setText("Great choice!");
                        bottomPopup.body.setText("Let's try another.");
                        bottomPopup.button.setText("Next");

                        bottomPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(bottomPopup);
                                buttonNo.setChecked(false);
                                setSecondPricesCompare();
                            }
                        });

                        break;
                }
                return true;
            }
        });
    }

    private void setSecondPricesCompare() {
        textviewFood.setText("Soup");
        textviewPrice.setText("$10.82");

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        fadeInView(centerPopup, 1f);
                        fadeInView(fullScreenGray, 0.9f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        centerPopup.header.setText("Another great choice!");
                        centerPopup.body.setText("Let's proceed to part two.");
                        centerPopup.button.setText("Next");

                        centerPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(centerPopup);
                                fadeOutView(fullScreenGray);
                                buttonYes.setChecked(false);
                                setFirstPriceMatch();
                            }
                        });

                        break;
                }
                return true;
            }
        });

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        fadeInView(centerPopup, 1f);
                        fadeInView(fullScreenGray, 0.9f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        centerPopup.header.setText("Another great choice!");
                        centerPopup.body.setText("Let's proceed to part two.");
                        centerPopup.button.setText("Next");

                        centerPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(centerPopup);
                                fadeOutView(fullScreenGray);
                                buttonNo.setChecked(false);
                                setFirstPriceMatch();
                            }
                        });

                        break;
                }
                return true;
            }
        });
    }

    private void setFirstPriceMatch() {
        textviewFood.setText("Bananas");
        textviewPrice.setVisibility(View.GONE);

        buttonYes.setText("$6.78");
        buttonNo.setText("$3.27");

        fadeInView(bottomPopup, 1f);

        // TODO
        // This is the wrong  type of popup
        bottomPopup.header.setText("What do you think?");
        bottomPopup.body.setText("Try your best to recall the price from part one.");
        bottomPopup.button.setText("Next");
        bottomPopup.button.setOnClickListener(null);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        // fadeInView(bottomPopup, 1f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        bottomPopup.header.setText("Great choice!");
                        bottomPopup.body.setText("Let's try another.");
                        bottomPopup.button.setText("Next");

                        bottomPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(bottomPopup);
                                buttonYes.setChecked(false);
                                setSecondPriceMatch();
                            }
                        });

                        break;
                }
                return true;
            }
        });

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        // fadeInView(bottomPopup, 1f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        bottomPopup.header.setText("Great choice!");
                        bottomPopup.body.setText("Let's try another.");
                        bottomPopup.button.setText("Next");

                        bottomPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(bottomPopup);
                                buttonNo.setChecked(false);
                                setSecondPriceMatch();
                            }
                        });

                        break;
                }
                return true;
            }
        });

    }

    private void setSecondPriceMatch() {
        textviewFood.setText("Soup");
        textviewPrice.setVisibility(View.GONE);

        buttonYes.setText("$10.82");
        buttonNo.setText("$4.01");

        fadeInView(bottomPopup, 1f);

        // TODO
        // This is the wrong  type of popup
        bottomPopup.header.setText("What do you think?");
        bottomPopup.body.setText("Choose the price that you saw in part one.");
        bottomPopup.button.setText("Next");
        bottomPopup.button.setOnClickListener(null);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        fadeOutView(bottomPopup);
                        fadeInView(centerPopup, 1f);
                        fadeInView(fullScreenGray, 0.9f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        centerPopup.header.setText("Tutorial Complete!");
                        centerPopup.body.setText("Placeholder popup!");
                        centerPopup.button.setText("Close");

                        centerPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(centerPopup);
                                fadeOutView(fullScreenGray);
                                NavigationManager.getInstance().popBackStack();
                            }
                        });

                        break;
                }
                return true;
            }
        });

        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        fadeOutView(bottomPopup);
                        fadeInView(centerPopup, 1f);
                        fadeInView(fullScreenGray, 0.9f);

                        progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 150;

                        centerPopup.header.setText("Tutorial Complete!");
                        centerPopup.body.setText("Placeholder popup!");
                        centerPopup.button.setText("Close");

                        centerPopup.button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fadeOutView(centerPopup);
                                fadeOutView(fullScreenGray);
                                NavigationManager.getInstance().popBackStack();
                            }
                        });

                        break;
                }
                return true;
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
