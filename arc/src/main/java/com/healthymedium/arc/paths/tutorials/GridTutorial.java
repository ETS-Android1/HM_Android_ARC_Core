package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.custom.DialogButtonTutorial;
import com.healthymedium.arc.custom.TutorialProgressView;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class GridTutorial extends BaseFragment {

    int selectedCount;

    RelativeLayout itemsLayout;

    GridLayout gridLayout;
    GridLayout gridLayoutLetters;

    DialogButtonTutorial centerPopup;

    FrameLayout fullScreenGray;
    TutorialProgressView progressView;

    ImageView closeButton;
    ImageView checkmark;
    ImageView image33;
    ImageView image43;

    TextView tapThisF;
    TextView textViewComplete;
    TextView textViewInstructions;

    Button endButton;
    View loadingView;
    LinearLayout progressBar;

    private int shortAnimationDuration;

    HintPointer itemsHint;
    HintPointer gridsHint;

    HintPointer partTwoHint;

    HintHighlighter pulsateF;
    HintPointer tapThisFHint;
    HintPointer tapAllFsHint;

    HintPointer niceWorkHint;

    HintPointer secondItemsHint;

    HintPointer recallHint;
    HintHighlighter pulsateGridItem;
    HintPointer otherTwoHint;
    HintHighlighter gridHighlight;

    Handler handler;
    Runnable runnableProceedToPartTwo = new Runnable() {
        @Override
        public void run() {
            partTwoHint.setText(ViewUtil.getString(R.string.popup_tutorial_part2));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    partTwoHint.dismiss();

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            fadeOutView(fullScreenGray);
                            setInitialLetterLayout();
                        }
                    };
                    handler.postDelayed(runnable,600);
                }
            };

            partTwoHint.addButton(ViewUtil.getString(R.string.button_next), listener);
            partTwoHint.show();
        }
    };

    Runnable runnableTapTheFs = new Runnable() {
        @Override
        public void run() {
            fadeInView(fullScreenGray, 0.9f);

            niceWorkHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf3));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    niceWorkHint.dismiss();

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            fadeOutView(fullScreenGray);
                            fadeOutView(gridLayoutLetters);
                            fadeOutView(textViewInstructions);
                            setSecondItemLayout();
                        }
                    };
                    handler.postDelayed(runnable,600);
                }
            };

            niceWorkHint.addButton(ViewUtil.getString(R.string.button_next), listener);
            niceWorkHint.show();
        }
    };

    public GridTutorial() {
        setTransitionSet(TransitionSet.getFadingDefault(true));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_tutorial, container, false);

        itemsLayout = view.findViewById(R.id.itemsLayout);

        gridLayout = view.findViewById(R.id.gridLayout);
        gridLayoutLetters = view.findViewById(R.id.gridLettersLayout);

        centerPopup = view.findViewById(R.id.centerPopup);
        fullScreenGray = view.findViewById(R.id.fullScreenGray);

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(100,true); // TODO: reflect actual progress

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);
        image33 = view.findViewById(R.id.image33);
        image43 = view.findViewById(R.id.image43);

        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        textViewInstructions = view.findViewById(R.id.instructions);

        tapThisF = view.findViewById(R.id.tapThisF);

        endButton = view.findViewById(R.id.endButton);
        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        itemsHint = new HintPointer(getActivity(), itemsLayout, true, false);
        gridsHint = new HintPointer(getActivity(), image43, false, true);

        partTwoHint = new HintPointer(getActivity(), image43, false, true);

        pulsateF = new HintHighlighter(getActivity());
        tapThisFHint = new HintPointer(getActivity(), tapThisF, true, false);
        tapAllFsHint = new HintPointer(getActivity(), image43, false, true);

        niceWorkHint = new HintPointer(getActivity(), image43, false, true);

        secondItemsHint = new HintPointer(getActivity(), itemsLayout, true, false);

        recallHint = new HintPointer(getActivity(), image33, true, false);
        pulsateGridItem = new HintHighlighter(getActivity());
        otherTwoHint = new HintPointer(getActivity(), gridLayout, true, true);
        gridHighlight = new HintHighlighter(getActivity());

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsHint.dismiss();
                gridsHint.dismiss();

                partTwoHint.dismiss();

                pulsateF.dismiss();
                tapThisFHint.dismiss();
                tapAllFsHint.dismiss();

                niceWorkHint.dismiss();

                secondItemsHint.dismiss();

                recallHint.dismiss();
                pulsateGridItem.dismiss();
                otherTwoHint.dismiss();
                gridHighlight.dismiss();

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        loadingView.animate()
                .setStartDelay(400)
                .setDuration(400)
                .translationYBy(-loadingView.getHeight());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setInitialItemLayout();
            }
        },1200);
    }

    private void setInitialItemLayout() {
        itemsHint.setText(ViewUtil.getString(R.string.popup_tutorial_grid_recall));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        fadeOutView(itemsLayout);
                        setInitialGridLayout();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        itemsHint.addButton(ViewUtil.getString(R.string.popup_gotit), listener);
        itemsHint.show();

    }

    private void setInitialGridLayout() {
        fadeInView(gridLayout, 1f);
        fadeInView(fullScreenGray, 0.9f);

        getImageView(3,0).setImageResource(R.drawable.phone);
        getImageView(2,2).setImageResource(R.drawable.pen);
        getImageView(1,3).setImageResource(R.drawable.key);

        gridsHint.setText(ViewUtil.getString(R.string.popup_tutorial_rememberbox));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridsHint.dismiss();
                fadeOutView(fullScreenGray);

                Handler handler = new Handler();
                handler.postDelayed(runnableProceedToPartTwo,3000);
            }
        };

        gridsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);
        gridsHint.show();
    }

    private void setInitialLetterLayout() {
        fadeOutView(gridLayout);
        fadeInView(gridLayoutLetters, 1f);

        textViewInstructions.setText(ViewUtil.getString(R.string.grids_subheader_fs));
        fadeInView(textViewInstructions, 1f);

        fadeOutView(fullScreenGray);

        Typeface font = Fonts.georgia;
        int size = gridLayoutLetters.getChildCount();
        for(int i=0;i<size;i++){
            ((TextView)gridLayoutLetters.getChildAt(i)).setTypeface(font);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pulsateF.addPulsingTarget(tapThisF,25);
                pulsateF.show();

                tapThisFHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf1));
                tapThisFHint.show();

                View.OnTouchListener listener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        int action = event.getAction();
                        switch (action){
                            case MotionEvent.ACTION_DOWN:
                                if((view.getTag() == null) || (view.getTag().equals(false))){
                                    view.setTag(true);
                                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));

                                    pulsateF.dismiss();
                                    tapThisFHint.dismiss();

                                    tapAllFsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf2));

                                    View.OnClickListener listener = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            tapAllFsHint.dismiss();
                                            tapLetters();
                                        }
                                    };

                                    tapAllFsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);
                                    tapAllFsHint.show();

                                    return false;
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                break;
                        }
                        return false;
                    }
                };

                tapThisF.setOnTouchListener(listener);

            }
        },500);


    }

    private void tapLetters() {
        handler = new Handler();
        handler.postDelayed(runnableTapTheFs,3000);

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        if(view.getTag() == null){
                            view.setTag(true);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                            return false;
                        }
                        if(view.getTag().equals(false)) {
                            view.setTag(true);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                        } else {
                            view.setTag(false);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        };

        int size = gridLayoutLetters.getChildCount();
        for(int i=0;i<size;i++){
            gridLayoutLetters.getChildAt(i).setOnTouchListener(listener);
        }
    }

    private void setSecondItemLayout() {
        fadeInView(itemsLayout, 1f);

        secondItemsHint.setText(ViewUtil.getString(R.string.popup_tutorial_selectbox));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondItemsHint.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        fadeOutView(itemsLayout);
                        setGridRecall();
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        secondItemsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), listener);
        secondItemsHint.show();
    }

    private void setGridRecall() {
        // TODO
        // Need to build the Remind Me functionality
        // Probably somewhere in this function

        fadeInView(gridLayout, 1f);

        textViewInstructions.setText(ViewUtil.getString(R.string.grids_subheader_boxes));
        fadeInView(textViewInstructions, 1f);

        fadeOutView(fullScreenGray);
        gridLayout.setVisibility(View.VISIBLE);

        getImageView(3,0).setImageResource(0);
        getImageView(2,2).setImageResource(0);
        getImageView(1,3).setImageResource(0);

        selectedCount = 0;

        recallHint.setText(ViewUtil.getString(R.string.popup_tutorial_boxhint));
        recallHint.show();

        getImageView(2,2).setImageResource(R.drawable.pen);
        pulsateGridItem.addPulsingTarget(image33);
        pulsateGridItem.show();

        View.OnTouchListener image33Listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        recallHint.dismiss();
                        pulsateGridItem.dismiss();
                        getImageView(2,2).setImageResource(0);
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridSelected));
                        selectedCount += 1;

                        // Now tap on the locations of the other two items.
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                gridHighlight.addTarget(gridLayout, 10, 10);
                                otherTwoHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapbox));
                                otherTwoHint.show();
                                gridHighlight.show();

                                // Disappear after a few seconds
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        otherTwoHint.dismiss();
                                        gridHighlight.dismiss();
                                    }
                                };
                                handler.postDelayed(runnable,3000);
                            }
                        };
                        handler.postDelayed(runnable,1000);

                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                if (selectedCount == 3) {
                    showComplete();
                }

                return false;
            }
        };

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridSelected));
                        selectedCount += 1;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                if (selectedCount == 3) {
                    showComplete();
                }

                return false;
            }
        };

        gridLayout.getChildAt(8).setTag(R.id.tag_color,R.color.gridNormal);
        gridLayout.getChildAt(8).setOnTouchListener(listener);

        gridLayout.getChildAt(12).setTag(R.id.tag_color,R.color.gridNormal);
        gridLayout.getChildAt(12).setOnTouchListener(image33Listener);

        gridLayout.getChildAt(15).setTag(R.id.tag_color,R.color.gridNormal);
        gridLayout.getChildAt(15).setOnTouchListener(listener);
    }

    private ImageView getImageView(int row, int col){
        return (ImageView)gridLayout.getChildAt((5*row)+col);
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

    private void showComplete() {
        fadeInView(checkmark, 1f);
        fadeInView(textViewComplete, 1f);
        fadeInView(endButton, 1f);

        fadeOutView(gridLayout);
        fadeOutView(textViewInstructions);

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
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
