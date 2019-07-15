package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

    Button endButton;
    View loadingView;
    LinearLayout progressBar;

    private int shortAnimationDuration;

    Handler handler;
    Runnable runnableProceedToPartTwo = new Runnable() {
        @Override
        public void run() {
            final HintPointer partTwoHint = new HintPointer(getActivity(), image43, false, true);
            partTwoHint.setRadius(16);
            partTwoHint.setText("<b>Great!</b><br>Let's proceed to part two.");

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

            partTwoHint.addButton("Next", listener);
            partTwoHint.show();
        }
    };

    Runnable runnableTapTheFs = new Runnable() {
        @Override
        public void run() {
            fadeInView(fullScreenGray, 0.9f);

            final HintPointer niceWorkHint = new HintPointer(getActivity(), image43, false, true);
            niceWorkHint.setText("Nice work! Don't worry if you didn't find them all.");

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
                            setSecondItemLayout();
                        }
                    };
                    handler.postDelayed(runnable,600);
                }
            };

            niceWorkHint.addButton("Next", listener);
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
        tapThisF = view.findViewById(R.id.tapThisF);

        endButton = view.findViewById(R.id.endButton);
        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        final HintPointer itemsHint = new HintPointer(getActivity(), itemsLayout, true, false);
        itemsHint.setRadius(16);
        itemsHint.setText("In this three part test, you'll be asked to <b>recall the location</b> of these items.");

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

        itemsHint.addButton("Got It", listener);
        itemsHint.show();

    }

    private void setInitialGridLayout() {
        fadeInView(gridLayout, 1f);
        fadeInView(fullScreenGray, 0.9f);

        getImageView(3,0).setImageResource(R.drawable.phone);
        getImageView(2,2).setImageResource(R.drawable.pen);
        getImageView(1,3).setImageResource(R.drawable.key);

        final HintPointer gridsHint = new HintPointer(getActivity(), image43, false, true);
        gridsHint.setRadius(16);
        gridsHint.setText("The items will be placed in a grid of boxes. <b>Remember which box each item is in.</b> You will have 3 seconds.");

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridsHint.dismiss();
                fadeOutView(fullScreenGray);

                Handler handler = new Handler();
                handler.postDelayed(runnableProceedToPartTwo,3000);
            }
        };

        gridsHint.addButton("I'm Ready", listener);
        gridsHint.show();
    }

    private void setInitialLetterLayout() {
        fadeOutView(gridLayout);
        fadeInView(gridLayoutLetters, 1f);
        fadeOutView(fullScreenGray);

        Typeface font = Fonts.georgia;
        int size = gridLayoutLetters.getChildCount();
        for(int i=0;i<size;i++){
            ((TextView)gridLayoutLetters.getChildAt(i)).setTypeface(font);
        }

        final HintHighlighter pulsateF = new HintHighlighter(getActivity());
        pulsateF.addPulsingTarget(tapThisF);
        pulsateF.show();

        final HintPointer tapThisFHint = new HintPointer(getActivity(), tapThisF, true, false);
        tapThisFHint.setText("Tap this letter F.");
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

                            final HintPointer tapAllFsHint = new HintPointer(getActivity(), image43, false, true);
                            tapAllFsHint.setText("Now: Tap all the F's you see as quickly as you can. You will have 3 seconds.");

                            View.OnClickListener listener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    tapAllFsHint.dismiss();
                                    tapLetters();
                                }
                            };

                            tapAllFsHint.addButton("I'm Ready", listener);
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

        final HintPointer secondItemsHint = new HintPointer(getActivity(), itemsLayout, true, false);
        secondItemsHint.setText("In the final part of the test, you will select the three boxes where these items were located in part one.");

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

        secondItemsHint.addButton("I'm Ready", listener);
        secondItemsHint.show();
    }

    private void setGridRecall() {
        fadeInView(gridLayout, 1f);
        fadeOutView(fullScreenGray);
        gridLayout.setVisibility(View.VISIBLE);

        getImageView(3,0).setImageResource(0);
        getImageView(2,2).setImageResource(0);
        getImageView(1,3).setImageResource(0);

        selectedCount = 0;

        final HintPointer recallHint = new HintPointer(getActivity(), image33, true, false);
        recallHint.setText("Hint: One item was located in this box. Tap here.");
        recallHint.show();

        final HintHighlighter pulsateGridItem = new HintHighlighter(getActivity());
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
