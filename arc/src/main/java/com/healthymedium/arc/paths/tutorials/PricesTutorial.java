package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.custom.RadioButton;
import com.healthymedium.arc.custom.TutorialProgressView;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class PricesTutorial extends BaseFragment {

    RelativeLayout priceContainer;

    RadioButton buttonYes;
    RadioButton buttonNo;

    TextView textviewFood;
    TextView textviewPrice;
    TextView textViewComplete;
    TextView textView12;

    FrameLayout fullScreenGray;
    TutorialProgressView progressView;

    ImageView closeButton;
    ImageView checkmark;

    Button endButton;

    private int shortAnimationDuration;

    HintHighlighter firstPriceContainerHighlight;
    HintPointer firstPriceHint;
    HintPointer firstGreatChoiceHint;

    HintHighlighter secondPriceContainerHighlight;
    HintPointer secondPriceHint;
    HintPointer secondGreatChoiceHint;

    HintHighlighter firstMatchContainerHighlight;
    HintPointer firstMatchHint;
    HintPointer firstMatchGreatChoiceHint;

    HintHighlighter secondMatchContainerHighlight;
    HintPointer secondMatchHint;

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

        priceContainer = view.findViewById(R.id.priceContainer);

        buttonYes = view.findViewById(R.id.radioButtonYes);
        buttonYes.setText(ViewUtil.getString(R.string.YES));
        buttonYes.setCheckable(false);

        buttonNo = view.findViewById(R.id.radioButtonNo);
        buttonNo.setText(ViewUtil.getString(R.string.NO));
        buttonNo.setCheckable(false);

        fullScreenGray = view.findViewById(R.id.fullScreenGray);

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(100,true); // TODO: reflect actual progress

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        firstPriceContainerHighlight = new HintHighlighter(getActivity());
        firstPriceHint = new HintPointer(getActivity(), priceContainer, true, false);
        firstGreatChoiceHint = new HintPointer(getActivity(), priceContainer, false, false);

        secondPriceContainerHighlight = new HintHighlighter(getActivity());
        secondPriceHint = new HintPointer(getActivity(), priceContainer, true, false);
        secondGreatChoiceHint = new HintPointer(getActivity(), priceContainer, false, false);

        firstMatchContainerHighlight = new HintHighlighter(getActivity());
        firstMatchHint = new HintPointer(getActivity(), priceContainer, true, false);
        firstMatchGreatChoiceHint = new HintPointer(getActivity(), priceContainer, false, false);

        secondMatchContainerHighlight = new HintHighlighter(getActivity());
        secondMatchHint = new HintPointer(getActivity(), priceContainer, true, false);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPriceContainerHighlight.dismiss();
                firstPriceHint.dismiss();
                firstGreatChoiceHint.dismiss();

                secondPriceContainerHighlight.dismiss();
                secondPriceHint.dismiss();
                secondGreatChoiceHint.dismiss();

                firstMatchContainerHighlight.dismiss();
                firstMatchHint.dismiss();
                firstMatchGreatChoiceHint.dismiss();

                secondMatchContainerHighlight.dismiss();
                secondMatchHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        NavigationManager.getInstance().popBackStack();
                    }
                };
                handler.postDelayed(runnable,300);
            }
        });

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        textView12=view.findViewById(R.id.textView12);

        textViewComplete = view.findViewById(R.id.textViewComplete);

        endButton = view.findViewById(R.id.endButton);

        setFirstPricesCompare();

        return view;
    }

    private void setFirstPricesCompare() {
        textviewFood.setText("Bananas");
        textviewPrice.setText("$3.27");

        firstPriceContainerHighlight.addTarget(priceContainer, 10);
        firstPriceContainerHighlight.show();

        firstPriceHint.setText("What do you think? Choose the answer that makes sense to you.");
        firstPriceHint.show();

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        firstPriceContainerHighlight.dismiss();
                        firstPriceHint.dismiss();

                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        firstGreatChoiceHint.setText("Great choice! Let's try another.");

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonYes.setChecked(false);
                                        setSecondPricesCompare();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        firstGreatChoiceHint.addButton("Next", listener);

                        firstGreatChoiceHint.show();

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
                        firstPriceContainerHighlight.dismiss();
                        firstPriceHint.dismiss();

                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        firstGreatChoiceHint.setText("Great choice! Let's try another.");

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonNo.setChecked(false);
                                        setSecondPricesCompare();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        firstGreatChoiceHint.addButton("Next", listener);

                        firstGreatChoiceHint.show();

                        break;
                }
                return true;
            }
        });
    }

    private void setSecondPricesCompare() {
        textviewFood.setText("Soup");
        textviewPrice.setText("$10.82");

        // priceContainerHighlight = new HintHighlighter(getActivity());
        secondPriceContainerHighlight.addTarget(priceContainer, 10);
        secondPriceContainerHighlight.show();

        // final HintPointer priceHint = new HintPointer(getActivity(), priceContainer, true, false);
        secondPriceHint.setText("What do you think? Choose the answer that makes sense to you.");
        secondPriceHint.show();

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        secondPriceContainerHighlight.dismiss();
                        secondPriceHint.dismiss();

                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        secondGreatChoiceHint.setText("Another great choice! Let's proceed to part two.");

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                secondGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonYes.setChecked(false);
                                        setFirstPriceMatch();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        secondGreatChoiceHint.addButton("Next", listener);

                        secondGreatChoiceHint.show();

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
                        secondPriceContainerHighlight.dismiss();
                        secondPriceHint.dismiss();

                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        secondGreatChoiceHint.setText("Another great choice! Let's proceed to part two.");

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                secondGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonNo.setChecked(false);
                                        setFirstPriceMatch();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        secondGreatChoiceHint.addButton("Next", listener);

                        secondGreatChoiceHint.show();

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

        // priceContainerHighlight = new HintHighlighter(getActivity());
        firstMatchContainerHighlight.addTarget(priceContainer, 10);
        firstMatchContainerHighlight.show();

        // final HintPointer priceHint = new HintPointer(getActivity(), priceContainer, true, false);
        firstMatchHint.setText("What do you think? Try your best to recall the price from part one.");
        firstMatchHint.show();

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();

                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        firstMatchGreatChoiceHint.setText("Great choice! Let's try another");

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstMatchGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonYes.setChecked(false);
                                        setSecondPriceMatch();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        firstMatchGreatChoiceHint.addButton("Next", listener);

                        firstMatchGreatChoiceHint.show();

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
                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();

                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        firstMatchGreatChoiceHint.setText("Great choice! Let's try another");

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firstMatchGreatChoiceHint.dismiss();

                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonNo.setChecked(false);
                                        setSecondPriceMatch();
                                    }
                                };
                                handler.postDelayed(runnable,600);
                            }
                        };

                        firstMatchGreatChoiceHint.addButton("Next", listener);

                        firstMatchGreatChoiceHint.show();

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

        // priceContainerHighlight = new HintHighlighter(getActivity());
        secondMatchContainerHighlight.addTarget(priceContainer, 10);
        secondMatchContainerHighlight.show();

        // final HintPointer priceHint = new HintPointer(getActivity(), priceContainer, true, false);
        secondMatchHint.setText("What do you think? Choose the price that you saw in part one.");
        secondMatchHint.show();

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();

                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        // fadeOutView(bottomPopup);

                        fadeOutView(textView12);
                        fadeOutView(buttonNo);
                        fadeOutView(buttonYes);
                        fadeOutView(textviewFood);
                        fadeOutView(textviewPrice);

                        fadeInView(checkmark, 1f);
                        checkmark.bringToFront();
                        fadeInView(textViewComplete, 1f);
                        textViewComplete.bringToFront();
                        fadeInView(endButton, 1f);

                        endButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();

                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);

                        buttonYes.setOnTouchListener(null);
                        buttonNo.setOnTouchListener(null);

                        // fadeOutView(bottomPopup);

                        fadeOutView(textView12);
                        fadeOutView(buttonNo);
                        fadeOutView(buttonYes);
                        fadeOutView(textviewFood);
                        fadeOutView(textviewPrice);

                        fadeInView(checkmark, 1f);
                        checkmark.bringToFront();
                        fadeInView(textViewComplete, 1f);
                        textViewComplete.bringToFront();
                        fadeInView(endButton, 1f);

                        endButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
