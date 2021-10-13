package com.healthymedium.arc.paths.templates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.TutorialProgressView;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class TutorialTemplate extends BaseFragment {

    private List<HintHighlighter> hintHighlighters = new ArrayList<>();
    private List<HintPointer> hintPointers = new ArrayList<>();
    private List<Handler> handlers = new ArrayList<>();
    protected ViewGroup container;

    protected HintHighlighter welcomeHighlight;
    protected HintPointer welcomeHint;
    protected HintHighlighter quitHighlight;
    protected HintPointer quitHint;

    protected TutorialProgressView progressView;
    protected ImageView closeButton;
    protected View loadingView;

    protected LinearLayout progressBar;
    protected int progress = 0;

    protected ImageView checkmark;
    protected TextView textViewComplete;
    protected Button endButton;

    protected Handler handler = new Handler();




    public TutorialTemplate() {
        setTransitionSet(TransitionSet.getFadingDefault(true));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        this.container = view.findViewById(R.id.containerLayout);

//        fullScreenGray = view.findViewById(R.id.fullScreenGray);
//        fullScreenGray.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        progressView = view.findViewById(R.id.progressView);
        progressView.setProgress(5,false);

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        textViewComplete = view.findViewById(R.id.textViewComplete);
        textViewComplete.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_tutorial_complete)));

        endButton = view.findViewById(R.id.endButton);
        endButton.setText(ViewUtil.getHtmlString(R.string.button_close));

        progressBar = view.findViewById(R.id.progressBar);
        loadingView = view.findViewById(R.id.loadingView);

        welcomeHighlight = new HintHighlighter(getActivity());
        welcomeHint = new HintPointer(getActivity(), progressView, true, false);

        quitHighlight = new HintHighlighter(getActivity());
        quitHint = new HintPointer(getActivity(), closeButton, true, false);

        hintHighlighters.add(welcomeHighlight);
        hintHighlighters.add(quitHighlight);

        hintPointers.add(welcomeHint);
        hintPointers.add(quitHint);

        handlers.add(handler);

        progressBar.animate()
                .setStartDelay(800)
                .setDuration(400)
                .alpha(1.0f);

        return view;
    }

    @Override
    protected void onEnterTransitionStart(boolean popped) {
        super.onEnterTransitionStart(popped);
        String hintTag = getClosePreventionHintTag();
        if(!Hints.hasBeenShown(hintTag)) {
            closeButton.setVisibility(View.GONE);
            Hints.markShown(hintTag);
        }
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        loadingView.animate()
                .setStartDelay(400)
                .setDuration(400)
                .translationYBy(-loadingView.getHeight());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupInitialLayout();
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(getTag(),"closeButton pressed");
                        closeButton.setEnabled(false);
                        for(Handler handler : handlers) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        for(HintHighlighter highlighter : hintHighlighters) {
                            highlighter.dismiss();
                        }
                        for(HintPointer pointer : hintPointers) {
                            pointer.dismiss();
                        }
                        exit();
                    }
                });
            }
        },1200);
    }

    // Display the hints for the progress bar and quit button
    protected void showProgressTutorial(final String tag, final Runnable nextSection) {
        Log.d(getTag(),"showProgressTutorial");
        welcomeHighlight.addTarget(progressView, 10, 2);
        welcomeHint.setText(ViewUtil.getString(R.string.popup_tutorial_welcome));

        View.OnClickListener welcomeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeHint.dismiss();
                welcomeHighlight.dismiss();
                Hints.markShown(tag);

                Handler handler = new Handler();
                handler.postDelayed(nextSection,600);
            }
        };

        welcomeHint.addButton(ViewUtil.getString(R.string.popup_gotit), welcomeListener);
        welcomeHighlight.show();
        welcomeHint.show();
    }

    protected void showCloseTutorial(final String tag, final Runnable nextSection) {
        Log.d(getTag(),"showCloseTutorial");

        quitHighlight.addTarget(closeButton, 50, 10);
        quitHint.setText(ViewUtil.getString(R.string.popup_tutorial_quit));

        View.OnClickListener quitListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitHint.dismiss();
                quitHighlight.dismiss();
                Hints.markShown(tag);

                Handler handler = new Handler();
                handler.postDelayed(nextSection,600);
            }
        };

        quitHint.addButton(ViewUtil.getString(R.string.popup_gotit), quitListener);
        quitHighlight.show();
        quitHint.show();
    }


    protected void fadeInView(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        ViewGroup parent = (ViewGroup) view.getParent();
        if(parent!=null){
            parent.removeView(view);
        }

        container.addView(view);
        view.animate()
                .alpha(1.0f)
                .setDuration(200)
                .setListener(null);
    }

    protected void fadeOutView(final View view) {
        view.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        container.removeView(view);
                    }
                });
    }

    // Displays the tutorial complete screen
    protected void showComplete() {
        Log.d(getTag(),"showComplete");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkmark.setAlpha(0f);
                checkmark.setVisibility(View.VISIBLE);
                checkmark.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .setListener(null);

                textViewComplete.setAlpha(0f);
                textViewComplete.setVisibility(View.VISIBLE);
                textViewComplete.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .setListener(null);

                endButton.setAlpha(0f);
                endButton.setVisibility(View.VISIBLE);
                endButton.animate()
                        .alpha(1.0f)
                        .setDuration(200)
                        .setListener(null);
                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        endButton.setEnabled(false);
                        exit();
                    }
                });
            }
        }, 1000);
    }

    protected void exit(){
        Log.d(getTag(),"exit");

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

    protected void incrementProgress(){
        Log.d(getTag(),"incrementProgress");

        progress += getProgressIncrement();
        progressView.setProgress(progress,true);
    }

    public void register(HintHighlighter highlighter) {
        hintHighlighters.add(highlighter);
    }

    public void register(HintPointer pointer) {
        hintPointers.add(pointer);
    }

    public void register(Handler handler) {
        handlers.add(handler);
    }

    protected abstract String getClosePreventionHintTag();

    protected abstract int getProgressIncrement();

    protected abstract void setupInitialLayout();


}
