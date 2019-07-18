package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.custom.RadioButton;
import com.healthymedium.arc.custom.TutorialProgressView;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class PricesTutorial extends BaseFragment {

    private static final String HINT_FIRST_TUTORIAL = "HINT_FIRST_TUTORIAL";

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
    View loadingView;
    LinearLayout progressBar;

    private int shortAnimationDuration;

    HintHighlighter welcomeHighlight;
    HintPointer welcomeHint;

    HintHighlighter quitHighlight;
    HintPointer quitHint;

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
        setTransitionSet(TransitionSet.getFadingDefault(true));
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

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

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
                welcomeHighlight.dismiss();
                welcomeHint.dismiss();

                quitHighlight.dismiss();
                quitHint.dismiss();

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

                exit();
            }
        });

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        textView12=view.findViewById(R.id.textView12);

        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        endButton = view.findViewById(R.id.endButton);
        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        progressBar.animate()
                .setStartDelay(800)
                .setDuration(400)
                .alpha(1.0f);

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        loadingView.animate()
                .setStartDelay(400)
                .translationYBy(-loadingView.getHeight())
                .setDuration(400);

        if (!Hints.hasBeenShown(HINT_FIRST_TUTORIAL)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showTutorial();
                }
            }, 1200);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setFirstPricesCompare();
                }
            }, 1200);
        }
    }

    private void showTutorial() {
        welcomeHighlight.addTarget(progressView, 10, 2);
        welcomeHint.setText(ViewUtil.getString(R.string.popup_tutorial_welcome));

        quitHighlight.addTarget(closeButton, 50, 10);
        quitHint.setText(ViewUtil.getString(R.string.popup_tutorial_quit));

        View.OnClickListener quitListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitHint.dismiss();
                quitHighlight.dismiss();
                Hints.markShown(HINT_FIRST_TUTORIAL);

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        setFirstPricesCompare();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        quitHint.addButton(ViewUtil.getString(R.string.popup_gotit), quitListener);

        View.OnClickListener welcomeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeHint.dismiss();
                welcomeHighlight.dismiss();
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        quitHighlight.show();
                        quitHint.show();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        welcomeHint.addButton(ViewUtil.getString(R.string.popup_gotit), welcomeListener);

        welcomeHighlight.show();
        welcomeHint.show();
    }

    private void setFirstPricesCompare() {
        textviewFood.setText("Bananas");
        textviewPrice.setText("$3.27");

        firstPriceContainerHighlight.addTarget(priceContainer, 10);
        firstPriceContainerHighlight.show();

        firstPriceHint.setText(ViewUtil.getString(R.string.popup_tutorial_choose1));
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

                        firstGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

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

                        firstGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

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

                        firstGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

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

                        firstGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

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

        secondPriceContainerHighlight.addTarget(priceContainer, 10);
        secondPriceContainerHighlight.show();

        secondPriceHint.setText(ViewUtil.getString(R.string.popup_tutorial_choose1));
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

                        secondGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice2));

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

                        secondGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

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

                        secondGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice2));

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

                        secondGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

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

        firstMatchContainerHighlight.addTarget(priceContainer, 10);
        firstMatchContainerHighlight.show();

        firstMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_recall));
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

                        firstMatchGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

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

                        firstMatchGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

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

                        firstMatchGreatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatchoice1));

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

                        firstMatchGreatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

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

        secondMatchContainerHighlight.addTarget(priceContainer, 10);
        secondMatchContainerHighlight.show();

        secondMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_choose2));
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
                                exit();
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
                                exit();
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

    private void exit(){
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
