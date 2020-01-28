package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.paths.informative.ContactScreen;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.ui.TimeInput;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

import static com.healthymedium.arc.core.Config.USE_HELP_SCREEN;

@SuppressLint("ValidFragment")
public class QuestionTime extends QuestionTemplate {

    private static final String HINT_QUESTION_TIME = "HINT_QUESTION_TIME";

    protected HintPointer pointer;
    protected TimeInput timeInput;
    protected LocalTime time;
    boolean enabled;
    boolean showHint;
    boolean didDismissPointer = false;

    public QuestionTime(boolean allowBack, String header, String subheader,@Nullable LocalTime defaultTime) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_submittime));
        time = defaultTime;
        type = "time";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);


        timeInput = new TimeInput(getContext());
        timeInput.setListener(new TimeInput.Listener() {
            public void onValidityChanged(boolean valid) {
                dismissPointer();
                if(buttonNext.isEnabled() != valid){
                    enabled = valid;
                    buttonNext.setEnabled(enabled);
                }
            }

            @Override
            public void onTimeChanged() {
                response_time = System.currentTimeMillis();
                dismissPointer();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(response_time==0.0){
                    response_time = System.currentTimeMillis();
                }
                dismissPointer();
                Study.getInstance().openNextFragment();

            }
        });

        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPointer();

                BaseFragment helpScreen;
                if (USE_HELP_SCREEN) {
                    helpScreen = new HelpScreen();
                } else {
                    helpScreen = new ContactScreen();
                }
                NavigationManager.getInstance().open(helpScreen);
            }
        });

        content.setGravity(Gravity.CENTER);
        content.addView(timeInput);



        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        if (didDismissPointer) {
            return;
        }

        if(!Hints.hasBeenShown(HINT_QUESTION_TIME)){
            pointer = new HintPointer(getMainActivity(),timeInput.getTimePicker(),true,false);
            pointer.setText(ViewUtil.getString(R.string.popup_scroll));
            pointer.show();
            Hints.markShown(HINT_QUESTION_TIME);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        time = timeInput.getTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(time !=null) {
            timeInput.setTime(time);
        }
        enabled = timeInput.isTimeValid();
        buttonNext.setEnabled(enabled);
    }

    @Override
    public Object onValueCollection(){
        if(timeInput!=null){
            time = timeInput.getTime();
        }
        if(time!=null){
            return time.toString("h:mm a");
        }
        return null;
    }

    protected void dismissPointer() {
        didDismissPointer = true;
        if(pointer!=null){
            pointer.dismiss();
            pointer = null;
        }
    }

}
