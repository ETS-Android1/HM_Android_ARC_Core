package com.healthymedium.arc.paths.tutorials;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.utilities.ViewUtil;

public class PricesTutorialRevised extends Tutorial {

    public static final String HINT_PREVENT_TUTORIAL_CLOSE_PRICES = "HINT_PREVENT_TUTORIAL_CLOSE_PRICES";
    private static final long WHAT_DO_YOU_THINK_HINT_DELAY = 10000;
    private static final long TAP_THIS_PRICE_HINT_DELAY = 4000;

    Runnable runnableWhatDoYouThink;
    Handler handlerWhatDoYouThink = new Handler();
    Runnable runnableCorrectAnswer;
    Handler handlerCorrectAnswer = new Handler();

    RelativeLayout priceContainer;

    RadioButton buttonYes;
    RadioButton buttonNo;

    TextView textviewFood;
    TextView textviewPrice;
    TextView textView12;

    HintPointer initialViewHint;

    HintHighlighter firstPriceContainerHighlight;

    HintPointer greatChoiceHint;

    HintHighlighter firstMatchContainerHighlight;
    HintPointer firstMatchHint;
    HintPointer firstMatchGreatChoiceHint;

    HintHighlighter secondMatchContainerHighlight;
    HintPointer secondMatchHint;


    HintHighlighter firstMatchAnswerHilighter;
    HintPointer firstMatchAnswerPointer;
    HintHighlighter secondMatchAnswerHilighter;
    HintPointer secondMatchAnswerPointer;



    public PricesTutorialRevised() {
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
        buttonYes.setText(ViewUtil.getString(R.string.radio_yes));
        buttonYes.setCheckable(false);
        buttonYes.setVisibility(View.INVISIBLE);

        buttonNo = view.findViewById(R.id.radioButtonNo);
        buttonNo.setText(ViewUtil.getString(R.string.radio_no));
        buttonNo.setCheckable(false);
        buttonNo.setVisibility(View.INVISIBLE);

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(5,false);
        progressIncrement = 25;

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

        firstPriceContainerHighlight = new HintHighlighter(getActivity());

        firstMatchContainerHighlight = new HintHighlighter(getActivity());
        firstMatchHint = new HintPointer(getActivity(), priceContainer, true, false);
        firstMatchGreatChoiceHint = new HintPointer(getActivity(), priceContainer, false, false);

        secondMatchContainerHighlight = new HintHighlighter(getActivity());
        secondMatchHint = new HintPointer(getActivity(), priceContainer, true, false);

        firstMatchAnswerHilighter = new HintHighlighter(getActivity());
        firstMatchAnswerPointer = new HintPointer(getActivity(), buttonNo, true, false);

        secondMatchAnswerHilighter = new HintHighlighter(getActivity());
        secondMatchAnswerPointer = new HintPointer(getActivity(), buttonYes, true, false);

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        initialViewHint = new HintPointer(getActivity(), textviewFood, false, false);
        greatChoiceHint = new HintPointer(getActivity(), textviewFood, false, false);

        textView12=view.findViewById(R.id.textView12);
        textView12.setVisibility(View.INVISIBLE);

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
    protected void onEnterTransitionStart(boolean popped) {
        super.onEnterTransitionStart(popped);
        if(!Hints.hasBeenShown(HINT_PREVENT_TUTORIAL_CLOSE_PRICES)) {
            closeButton.setVisibility(View.GONE);
            Hints.markShown(HINT_PREVENT_TUTORIAL_CLOSE_PRICES);
        }
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setFirstPricesCompare();
            }
        }, 1200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeButton.setEnabled(false);
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);

                        welcomeHighlight.dismiss();
                        welcomeHint.dismiss();

                        quitHighlight.dismiss();
                        quitHint.dismiss();

                        initialViewHint.dismiss();

                        firstPriceContainerHighlight.dismiss();

                        greatChoiceHint.dismiss();

                        firstMatchContainerHighlight.dismiss();
                        firstMatchHint.dismiss();
                        firstMatchGreatChoiceHint.dismiss();

                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();

                        exit();
                    }
                });
            }
        },1200);
    }

    private void setFirstPricesCompare() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item1));
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price1));

//        firstPriceContainerHighlight.addTarget(priceContainer, 10);
//        firstPriceContainerHighlight.addTarget(progressBar);
//        firstPriceContainerHighlight.show();

        initialViewHint.setText(ViewUtil.getString(R.string.popup_tutorial_price_intro));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialViewHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        incrementProgress();
                        setSecondPricesCompare();
                    }
                };
                handler.postDelayed(runnable,3000);
            }
        };

        initialViewHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);

        initialViewHint.show();
    }

    private void setSecondPricesCompare() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item2));
        textviewPrice.setText(ViewUtil.getString(R.string.prices_tutorial_price2));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                greatChoiceHint.setText(ViewUtil.getString(R.string.popup_tutorial_part2));

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        incrementProgress();
                        greatChoiceHint.dismiss();

                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                setFirstPriceMatch();
                            }
                        };
                        handler.postDelayed(runnable,600);
                    }
                };

                greatChoiceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                greatChoiceHint.show();
            }
        }, 3000);
    }

    private void setFirstPriceMatch() {
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item1));
        textviewPrice.setVisibility(View.GONE);

        textView12.setVisibility(View.VISIBLE);
        buttonYes.setVisibility(View.VISIBLE);
        buttonNo.setVisibility(View.VISIBLE);

        textView12.setText(ViewUtil.getString(R.string.prices_whatwasprice));

        buttonYes.setText(ViewUtil.getString(R.string.prices_tutorial_price1_match));
        buttonNo.setText(ViewUtil.getString(R.string.prices_tutorial_price1));

        buttonYes.showButton(false);
        buttonYes.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);
        buttonNo.showButton(false);
        buttonNo.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);

        runnableWhatDoYouThink = new Runnable() {
            @Override
            public void run() {
                firstMatchContainerHighlight.addTarget(priceContainer, 10);
                firstMatchContainerHighlight.addTarget(progressBar);
                firstMatchContainerHighlight.show();

                firstMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_recall));
                firstMatchHint.show();
            }
        };

        handlerWhatDoYouThink.post(runnableWhatDoYouThink);

        runnableCorrectAnswer = new Runnable() {
            @Override
            public void run() {
                firstMatchContainerHighlight.dismiss();
                firstMatchHint.dismiss();
                firstMatchAnswerHilighter.addPulsingTarget(buttonNo.findViewById(R.id.frameLayoutRadioButton),42);
                firstMatchAnswerHilighter.addTarget(progressBar);
                firstMatchAnswerHilighter.show();

                firstMatchAnswerPointer.setText(ViewUtil.getString(R.string.popup_tutorial_pricetap));
                firstMatchAnswerPointer.show();
            }
        };

        handlerCorrectAnswer.postDelayed(runnableCorrectAnswer, TAP_THIS_PRICE_HINT_DELAY);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        handlerCorrectAnswer.post(runnableCorrectAnswer);

                        buttonYes.setChecked(false);

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
                        firstMatchAnswerHilighter.dismiss();
                        firstMatchAnswerPointer.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        incrementProgress();
                        updateButtons(false);

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
        textviewFood.setText(ViewUtil.getString(R.string.prices_tutorial_item2));
        textviewPrice.setVisibility(View.GONE);

        buttonYes.setText(ViewUtil.getString(R.string.prices_tutorial_price2));
        buttonNo.setText(ViewUtil.getString(R.string.prices_tutorial_price2_match));

        buttonYes.showButton(false);
        buttonYes.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);
        buttonNo.showButton(false);
        buttonNo.setLabelPosition(View.TEXT_ALIGNMENT_CENTER);

        runnableWhatDoYouThink = new Runnable() {
            @Override
            public void run() {
                secondMatchContainerHighlight.addTarget(priceContainer, 10);
                secondMatchContainerHighlight.addTarget(progressBar);
                secondMatchContainerHighlight.show();

                secondMatchHint.setText(ViewUtil.getString(R.string.popup_tutorial_choose2));
                secondMatchHint.show();
            }
        };

        handlerWhatDoYouThink.postDelayed(runnableWhatDoYouThink, WHAT_DO_YOU_THINK_HINT_DELAY);

        runnableCorrectAnswer = new Runnable() {
            @Override
            public void run() {
                secondMatchContainerHighlight.dismiss();
                secondMatchHint.dismiss();
                secondMatchAnswerHilighter.addPulsingTarget(buttonYes.findViewById(R.id.frameLayoutRadioButton),42);
                secondMatchAnswerHilighter.addTarget(progressBar);
                secondMatchAnswerHilighter.show();

                secondMatchAnswerPointer.setText(ViewUtil.getString(R.string.popup_tutorial_pricetap));
                secondMatchAnswerPointer.show();
            }
        };

        handlerCorrectAnswer.postDelayed(runnableCorrectAnswer, WHAT_DO_YOU_THINK_HINT_DELAY + TAP_THIS_PRICE_HINT_DELAY);

        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        secondMatchContainerHighlight.dismiss();
                        secondMatchHint.dismiss();
                        secondMatchAnswerHilighter.dismiss();
                        secondMatchAnswerPointer.dismiss();
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        incrementProgress();
                        updateButtons(true);

                        fadeOutView(textView12);
                        fadeOutView(buttonNo);
                        fadeOutView(buttonYes);
                        fadeOutView(textviewFood);
                        fadeOutView(textviewPrice);

                        showComplete();

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
                        handlerWhatDoYouThink.removeCallbacks(runnableWhatDoYouThink);
                        handlerCorrectAnswer.removeCallbacks(runnableCorrectAnswer);
                        handlerCorrectAnswer.post(runnableCorrectAnswer);

                        break;
                }
                return true;
            }
        });
    }

    private void updateButtons(Boolean isYesChecked) {
        if (isYesChecked) {
            buttonYes.setChecked(true);
            buttonNo.setChecked(false);
        } else {
            buttonYes.setChecked(false);
            buttonNo.setChecked(true);
        }

        buttonYes.setOnTouchListener(null);
        buttonNo.setOnTouchListener(null);
    }
}
