package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.custom.DialogButtonTutorial;
import com.healthymedium.arc.custom.SymbolTutorialButton;
import com.healthymedium.arc.custom.TutorialProgressView;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class SymbolTutorial extends BaseFragment {

    private static final String HINT_FIRST_TUTORIAL = "HINT_FIRST_TUTORIAL";

    final Handler handlerOutline = new Handler();
    final Handler handlerPulsate = new Handler();
    final Handler handlerCoachmark = new Handler();

    Runnable runnableTileOutline;
    Runnable runnableTilePulsate;
    Runnable runnableCoachmark;

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
    TutorialProgressView progressView;

    ImageView closeButton;
    ImageView checkmark;

    TextView textView20;
    TextView textViewComplete;

    Button endButton;
    View loadingView;
    LinearLayout progressBar;

    private int shortAnimationDuration;

    HintHighlighter welcomeHighlight;
    HintPointer welcomeHint;

    HintHighlighter quitHighlight;
    HintPointer quitHint;

    HintHighlighter buttonTop2Highlight;
    HintPointer buttonTop2Hint;

    HintHighlighter topSymbolsHighlight;
    HintPointer topSymbolsHint;

    HintHighlighter bottomSymbolsHighlight;
    HintPointer bottomSymbolsHint;

    HintHighlighter initialTilesOutline;
    HintHighlighter initialTilesPulsate;

    HintHighlighter secondTilesOutline;
    HintHighlighter secondTilesPulsate;

    HintHighlighter finalTilesOutline;
    HintHighlighter finalTilesPulsate;

    public SymbolTutorial() {
        setTransitionSet(TransitionSet.getFadingDefault(true));
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

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(100,true); // TODO: reflect actual progress

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        textView20 = view.findViewById(R.id.textView20);
        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        endButton = view.findViewById(R.id.endButton);
        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_8_tutorial, R.drawable.ic_symbol_1_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_7_tutorial, R.drawable.ic_symbol_3_tutorial);

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

        buttonTop2Highlight = new HintHighlighter(getActivity());
        buttonTop2Hint = new HintPointer(getActivity(), buttonTop2, true, false);

        topSymbolsHighlight = new HintHighlighter(getActivity());
        topSymbolsHint = new HintPointer(getActivity(), topSymbols, true, false);

        bottomSymbolsHighlight = new HintHighlighter(getActivity());
        bottomSymbolsHint = new HintPointer(getActivity(), bottomSymbolsButtons, true, true);

        initialTilesOutline = new HintHighlighter(getActivity());
        initialTilesPulsate = new HintHighlighter(getActivity());

        secondTilesOutline = new HintHighlighter(getActivity());
        secondTilesPulsate = new HintHighlighter(getActivity());

        finalTilesOutline = new HintHighlighter(getActivity());
        finalTilesPulsate = new HintHighlighter(getActivity());

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerOutline.removeCallbacks(runnableTileOutline);
                handlerPulsate.removeCallbacks(runnableTilePulsate);
                handlerCoachmark.removeCallbacks(runnableCoachmark);

                welcomeHighlight.dismiss();
                welcomeHint.dismiss();

                quitHighlight.dismiss();
                quitHint.dismiss();

                buttonTop2Highlight.dismiss();
                buttonTop2Hint.dismiss();

                topSymbolsHighlight.dismiss();
                topSymbolsHint.dismiss();

                bottomSymbolsHighlight.dismiss();
                bottomSymbolsHint.dismiss();

                initialTilesOutline.dismiss();
                initialTilesPulsate.dismiss();

                secondTilesOutline.dismiss();
                secondTilesPulsate.dismiss();

                finalTilesOutline.dismiss();
                finalTilesPulsate.dismiss();

                exit();
            }
        });


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
                .setDuration(400)
                .translationYBy(-loadingView.getHeight());

        if (!Hints.hasBeenShown(HINT_FIRST_TUTORIAL)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showTutorial();
                }
            },1200);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stepMiddleTopTile();
                }
            },1200);
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
                        stepMiddleTopTile();
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

    private void stepMiddleTopTile() {

        buttonTop2Highlight.addTarget(buttonTop2, 10, 10);
        buttonTop2Highlight.show();

        buttonTop2Hint.setText(ViewUtil.getString(R.string.popup_tutorial_tile));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonTop2Highlight.dismiss();
                buttonTop2Hint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        stepAllTopTiles();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        buttonTop2Hint.addButton(ViewUtil.getString(R.string.button_next), listener);

        buttonTop2Hint.show();
    }

    private void stepAllTopTiles() {

        topSymbolsHighlight.addTarget(topSymbolsInnerLayout, 10, 0);
        topSymbolsHighlight.show();

        topSymbolsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tilestop));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topSymbolsHighlight.dismiss();
                topSymbolsHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        stepBottomTiles();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        topSymbolsHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        topSymbolsHint.show();
    }

    private void stepBottomTiles() {

        bottomSymbolsHighlight.addTarget(bottomSymbolsButtons, 10, 0);
        bottomSymbolsHighlight.show();

        bottomSymbolsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tilesbottom));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSymbolsHighlight.dismiss();
                bottomSymbolsHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        initialTiles();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        bottomSymbolsHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        bottomSymbolsHint.show();
    }

    private void initialTiles() {

        initialTilesOutline.addTarget(buttonBottom1);
        initialTilesOutline.addTarget(buttonTop3);

        initialTilesPulsate.addPulsingTarget(buttonBottom1);
        initialTilesPulsate.addTarget(buttonTop3);

        runnableTileOutline = new Runnable() {
            @Override
            public void run() {
                initialTilesOutline.show();
            }
        };

        runnableTilePulsate = new Runnable() {
            @Override
            public void run() {
                initialTilesOutline.dismiss();
                initialTilesPulsate.show();
            }
        };

        runnableCoachmark = new Runnable() {
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

                initialTilesOutline.dismiss();
                initialTilesPulsate.dismiss();

                // fadeInView(fullScreenGray, 0.9f);
                buttonBottom1.setOnClickListener(null);

                final HintPointer greatJobHint = new HintPointer(getActivity(), bottomSymbolsButtons, false, true);
                greatJobHint.setText(ViewUtil.getString(R.string.popup_tutorial_greatjob));

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        greatJobHint.dismiss();

                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                // fadeOutView(fullScreenGray);
                                secondTiles();
                            }
                        };
                        handler.postDelayed(runnable,600);
                    }
                };

                greatJobHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                greatJobHint.show();
            }
        });
    }

    private void secondTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_6_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        secondTilesOutline.addTarget(buttonBottom2);
        secondTilesOutline.addTarget(buttonTop2);

        secondTilesPulsate.addPulsingTarget(buttonBottom2);
        secondTilesPulsate.addTarget(buttonTop2);

        runnableTileOutline = new Runnable() {
            @Override
            public void run() {
                secondTilesOutline.show();
            }
        };

        runnableTilePulsate = new Runnable() {
            @Override
            public void run() {
                secondTilesOutline.dismiss();
                secondTilesPulsate.show();
            }
        };

        runnableCoachmark = new Runnable() {
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

                secondTilesOutline.dismiss();
                secondTilesPulsate.dismiss();

                buttonBottom2.setOnClickListener(null);

                final HintPointer niceHint = new HintPointer(getActivity(), bottomSymbolsButtons, false, true);
                niceHint.setText(ViewUtil.getString(R.string.popup_tutorial_nice));

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        niceHint.dismiss();

                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                fadeOutView(fullScreenGray);
                                lastTiles();
                            }
                        };
                        handler.postDelayed(runnable,600);
                    }
                };

                niceHint.addButton(ViewUtil.getString(R.string.button_next), listener);

                niceHint.show();
            }
        });
    }

    private void lastTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        finalTilesOutline.addTarget(buttonBottom1);
        finalTilesOutline.addTarget(buttonTop1);

        finalTilesPulsate.addPulsingTarget(buttonBottom1);
        finalTilesPulsate.addTarget(buttonTop1);

        runnableTileOutline = new Runnable() {
            @Override
            public void run() {
                finalTilesOutline.show();
            }
        };

        runnableTilePulsate = new Runnable() {
            @Override
            public void run() {
                finalTilesOutline.dismiss();
                finalTilesPulsate.show();
            }
        };

        runnableCoachmark = new Runnable() {
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

                finalTilesOutline.dismiss();
                finalTilesPulsate.dismiss();

                fadeInView(checkmark, 1f);
                fadeInView(textViewComplete, 1f);
                fadeInView(endButton, 1f);

                fadeOutView(topSymbols);
                fadeOutView(textView20);
                fadeOutView(bottomSymbolsButtons);

                buttonBottom1.setOnClickListener(null);

                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exit();
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
