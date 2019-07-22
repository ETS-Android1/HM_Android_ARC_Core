package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.custom.CircleProgressView;
import com.healthymedium.arc.custom.base.RoundedFrameLayout;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.study.Visit;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class DayProgressScreen extends BaseFragment {

    int margin = ViewUtil.dpToPx(4);

    CircleProgressView latestView;
    int latestProgress = 0;

    ImageView confetti;

    public DayProgressScreen() {
        allowBackPress(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_progress, container, false);

        // get inflated views ----------------------------------------------------------------------

        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        TextView textViewTestsComplete = view.findViewById(R.id.textViewTestsComplete);
        TextView textViewTestsLeft = view.findViewById(R.id.textViewTestsLeft);
        RoundedFrameLayout frameLayoutDone = view.findViewById(R.id.frameLayoutDone);
        Button button = view.findViewById(R.id.button);
        confetti = view.findViewById(R.id.imageViewConfetti);

        // display progress views ------------------------------------------------------------------

        Visit visit = Study.getCurrentVisit();

        List<TestSession> allSessions = visit.getTestSessions();
        List<TestSession> sessions = new ArrayList<>();

        int sessionsComplete = 0;
        int latestIndex = -1;


        // get an array of today's tests
        int dayIndex = visit.getDayIndex(LocalDate.now());
        for(TestSession session : allSessions){
            if(session.getDayIndex()==dayIndex){
                sessions.add(session);
            }
        }

        // find the last test to be competed
        DateTime latestComplete = new DateTime();
        for(int i=0;i<sessions.size();i++){
            TestSession session = sessions.get(i);
            if(session.wasFinished()){
                sessionsComplete++;
                DateTime completeTime = session.getCompletedTime();
                if(completeTime.isAfter(latestComplete)){
                    latestComplete = completeTime;
                    latestIndex = i;
                }
            }
        }

        // add progress views
        for(int i=0;i<sessions.size();i++){
            CircleProgressView progressView = new CircleProgressView(getContext());
            linearLayout.addView(progressView);

            progressView.setBaseColor(R.color.primary);
            progressView.setCheckmarkColor(R.color.secondary);
            progressView.setShadowColor(R.color.secondary);
            progressView.setSweepColor(R.color.secondaryAccent);
            progressView.setMargin(margin,0,margin,0);
            if(i!=latestIndex) {
                progressView.setProgress(sessions.get(i).getProgress(), false);
            } else {
                latestView = progressView;
                latestProgress = sessions.get(i).getProgress();
            }
        }

        // display proper test ---------------------------------------------------------------------

        textViewTestsComplete.setText(sessionsComplete + (sessionsComplete==1?" Session Complete!":" Sessions Complete!"));

        if(sessionsComplete==visit.getNumberOfTestsToday()){
            textViewTestsLeft.setVisibility(View.GONE);
            frameLayoutDone.setVisibility(View.VISIBLE);
        } else {
            String before= "Only ";
            String highlight =  visit.getNumberOfTestsLeftForToday()+" more ";
            String after= "to go today.";
            String text = before+highlight+after;
            Spannable spannable = new SpannableString(text);
            spannable.setSpan(new ForegroundColorSpan(
                    ViewUtil.getColor(getContext(),R.color.hintDark)),
                    text.indexOf(highlight),
                    text.indexOf(highlight)+ highlight.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewTestsLeft.setText(spannable);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Study.openNextFragment();
            }
        });
        confetti.animate().translationYBy(-200);

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        if(latestView!=null) {
            latestView.setProgress(latestProgress,true);
        }
        confetti.animate().translationYBy(200).setDuration(1000);
        confetti.animate().alpha(1.0f).setDuration(1000);
    }

}
