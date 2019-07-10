package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
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
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.NavigationManager;

public class SymbolTutorial extends BaseFragment {

    final Handler handlerOutline = new Handler();
    final Handler handlerPulsate = new Handler();
    final Handler handlerCoachmark = new Handler();

    RelativeLayout topSymbols;
    RelativeLayout topSymbolsInnerLayout;
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

    Handler handler;

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
        topSymbolsInnerLayout = view.findViewById(R.id.topSymbolsInnerLayout);
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

        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_8_tutorial, R.drawable.ic_symbol_1_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_7_tutorial, R.drawable.ic_symbol_3_tutorial);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        stepMiddleTopTile();

        return view;
    }

    private void stepMiddleTopTile() {
        //fadeInView(fullScreenGray, 0.9f);

        //fadeInView(buttonTop2, 1f);

        centerPopup.header.setText("This is a tile.");
        centerPopup.body.setText("Each tile includes a pair of symbols.");
        centerPopup.button.setText("Next");

        fadeInView(centerPopup, 1f);

        final HintHighlighter buttonTop2Highlight = new HintHighlighter(getActivity());
        buttonTop2Highlight.addTarget(buttonTop2, 15);
        buttonTop2Highlight.show();

        centerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeOutView(centerPopup);
                buttonTop2Highlight.dismiss();
                stepAllTopTiles();
            }
        });
    }

    private void stepAllTopTiles() {
        //fadeInView(buttonTop1, 1f);
        //fadeInView(buttonTop3, 1f);

        centerPopup.header.setText("You will see three tiles on the top of the screen...");
        centerPopup.body.setText("");
        centerPopup.button.setText("Next");

        fadeInView(centerPopup, 1f);

        final HintHighlighter topSymbolsHighlight = new HintHighlighter(getActivity());
        topSymbolsHighlight.addTarget(topSymbolsInnerLayout, 30);
        topSymbolsHighlight.show();

        centerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeOutView(centerPopup);
                topSymbolsHighlight.dismiss();
                stepBottomTiles();
            }
        });
    }

    private void stepBottomTiles() {
        centerPopup.header.setText("...and two tiles on the bottom.");
        centerPopup.body.setText("");
        centerPopup.button.setText("Next");

        fadeInView(centerPopup, 1f);

        final HintHighlighter bottomSymbolsHighlight = new HintHighlighter(getActivity());
        bottomSymbolsHighlight.addTarget(bottomSymbolsButtons, 30);
        bottomSymbolsHighlight.show();

        centerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeOutView(centerPopup);
                bottomSymbolsHighlight.dismiss();
                initialTiles();
            }
        });
    }

    private void initialTiles() {

        final HintHighlighter initialTilesOutline = new HintHighlighter(getActivity());
        initialTilesOutline.addTarget(buttonBottom1);
        initialTilesOutline.addTarget(buttonTop3);

        final HintHighlighter initialTilesPulsate = new HintHighlighter(getActivity());
        initialTilesPulsate.addPulsingTarget(buttonBottom1);
        initialTilesPulsate.addTarget(buttonTop3);

        final Runnable runnableTileOutline = new Runnable() {
            @Override
            public void run() {
                initialTilesOutline.show();
            }
        };

        final Runnable runnableTilePulsate = new Runnable() {
            @Override
            public void run() {
                initialTilesOutline.dismiss();
                initialTilesPulsate.show();
            }
        };

        final Runnable runnableCoachmark = new Runnable() {
            @Override
            public void run() {
            }
        };

        handlerOutline.postDelayed(runnableTileOutline,5000);
        handlerPulsate.postDelayed(runnableTilePulsate,10000);
        handlerCoachmark.postDelayed(runnableCoachmark, 15000);

        buttonBottom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handlerOutline.removeCallbacks(runnableTileOutline);
                handlerPulsate.removeCallbacks(runnableTilePulsate);
                handlerCoachmark.removeCallbacks(runnableCoachmark);

                fadeInView(centerPopup, 1f);
                // fadeInView(fullScreenGray, 0.9f);
                buttonBottom1.setOnClickListener(null);

                progressBarGradient.getLayoutParams().width = progressBarGradient.getLayoutParams().width + 200;

                centerPopup.header.setText("Great job!");
                centerPopup.body.setText("Let's try a couple more for practice.");
                centerPopup.button.setText("Next");

                centerPopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fadeOutView(centerPopup);
                        //fadeOutView(fullScreenGray);
                        secondTiles();
                    }
                });
            }
        });
    }

    private void secondTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_6_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        final HintHighlighter secondTilesOutline = new HintHighlighter(getActivity());
        secondTilesOutline.addTarget(buttonBottom2);
        secondTilesOutline.addTarget(buttonTop2);

        final HintHighlighter secondTilesPulsate = new HintHighlighter(getActivity());
        secondTilesPulsate.addPulsingTarget(buttonBottom2);

        final Runnable runnableTileOutline = new Runnable() {
            @Override
            public void run() {
                secondTilesOutline.show();
            }
        };

        final Runnable runnableTilePulsate = new Runnable() {
            @Override
            public void run() {
                secondTilesOutline.dismiss();
                secondTilesPulsate.show();
            }
        };

        final Runnable runnableCoachmark = new Runnable() {
            @Override
            public void run() {
            }
        };

        handlerOutline.postDelayed(runnableTileOutline,5000);
        handlerPulsate.postDelayed(runnableTilePulsate,10000);
        handlerCoachmark.postDelayed(runnableCoachmark, 15000);

        buttonBottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handlerOutline.removeCallbacks(runnableTileOutline);
                handlerPulsate.removeCallbacks(runnableTilePulsate);
                handlerCoachmark.removeCallbacks(runnableCoachmark);

                fadeInView(centerPopup, 1f);
                // fadeInView(fullScreenGray, 0.9f);
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
                        lastTiles();
                    }
                });
            }
        });
    }

    private void lastTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        final HintHighlighter finalTilesOutline = new HintHighlighter(getActivity());
        finalTilesOutline.addTarget(buttonBottom1);
        finalTilesOutline.addTarget(buttonTop1);

        final HintHighlighter finalTilesPulsate = new HintHighlighter(getActivity());
        finalTilesPulsate.addPulsingTarget(buttonBottom1);
        finalTilesPulsate.addTarget(buttonTop1);

        final Runnable runnableTileOutline = new Runnable() {
            @Override
            public void run() {
                finalTilesOutline.show();
            }
        };

        final Runnable runnableTilePulsate = new Runnable() {
            @Override
            public void run() {
                finalTilesOutline.dismiss();
                finalTilesPulsate.show();
            }
        };

        final Runnable runnableCoachmark = new Runnable() {
            @Override
            public void run() {
            }
        };

        handlerOutline.postDelayed(runnableTileOutline,5000);
        handlerPulsate.postDelayed(runnableTilePulsate,10000);
        handlerCoachmark.postDelayed(runnableCoachmark, 15000);

        buttonBottom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handlerOutline.removeCallbacks(runnableTileOutline);
                handlerPulsate.removeCallbacks(runnableTilePulsate);
                handlerCoachmark.removeCallbacks(runnableCoachmark);

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
