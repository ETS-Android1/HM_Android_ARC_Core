package com.healthymedium.arc.paths.tutorials;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.ui.SymbolButton;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.ui.SymbolView;
import com.healthymedium.arc.utilities.ViewUtil;

public class SymbolTutorial extends Tutorial {

    final Handler handlerOutline = new Handler();
    final Handler handlerPulsate = new Handler();
    final Handler handlerCoachmark = new Handler();

    Runnable runnableTileOutline;
    Runnable runnableTilePulsate;
    Runnable runnableCoachmark;

    RelativeLayout topSymbols;
    RelativeLayout topSymbolsInnerLayout;
    RelativeLayout bottomSymbolsButtons;

    SymbolView buttonTop1;
    SymbolView buttonTop2;
    SymbolView buttonTop3;
    SymbolButton buttonBottom1;
    SymbolButton buttonBottom2;

    TextView textView20;

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

        progressBar.animate()
                .setStartDelay(800)
                .setDuration(400)
                .alpha(1.0f);

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        if (!Hints.hasBeenShown(HINT_FIRST_TUTORIAL)) {
            final Runnable next = new Runnable() {
                @Override
                public void run() {
                    stepMiddleTopTile();
                }
            };

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showTutorial(next);
                }
            }, 1200);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stepMiddleTopTile();
                }
            },1200);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        },1200);
    }

    // Displays hints for the middle tile of the top set
    // Explains what a tile is
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

    // Displays hints for the entire top set of tiles
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

    // Displays hints for the entire bottom set of tiles
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

    // The first set of tiles to match
    private void initialTiles() {
        initialTilesOutline.addTarget(buttonBottom1,8);
        initialTilesOutline.addTarget(buttonTop3,8);

        initialTilesPulsate.addPulsingTarget(buttonBottom1,8);
        initialTilesPulsate.addTarget(buttonTop3,8);

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

    // The second set of tiles to match
    private void secondTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_6_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_2_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        secondTilesOutline.addTarget(buttonBottom2,8);
        secondTilesOutline.addTarget(buttonTop2,8);

        secondTilesPulsate.addPulsingTarget(buttonBottom2,8);
        secondTilesPulsate.addTarget(buttonTop2,8);

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

    // The final set of tiles to match
    private void lastTiles() {
        buttonTop1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop2.setImages(R.drawable.ic_symbol_2_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonTop3.setImages(R.drawable.ic_symbol_5_tutorial, R.drawable.ic_symbol_4_tutorial);
        buttonBottom1.setImages(R.drawable.ic_symbol_3_tutorial, R.drawable.ic_symbol_7_tutorial);
        buttonBottom2.setImages(R.drawable.ic_symbol_1_tutorial, R.drawable.ic_symbol_8_tutorial);

        finalTilesOutline.addTarget(buttonBottom1,8);
        finalTilesOutline.addTarget(buttonTop1,8);

        finalTilesPulsate.addPulsingTarget(buttonBottom1,8);
        finalTilesPulsate.addTarget(buttonTop1,8);

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

                fadeOutView(topSymbols);
                fadeOutView(textView20);
                fadeOutView(bottomSymbolsButtons);

                buttonBottom1.setOnClickListener(null);

                showComplete();
            }
        });
    }

}
