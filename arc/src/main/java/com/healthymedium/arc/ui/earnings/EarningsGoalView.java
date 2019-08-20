package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.ui.CircleProgressView;
import com.healthymedium.arc.ui.WeekProgressView;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsGoalView extends RoundedLinearLayout {

    static final String TWENTY_ONE_SESSIONS = "21-sessions";
    static final String TWO_A_DAY = "2-a-day";
    static final String FOUR_OUT_OF_FOUR = "4-out-of-4";

    TextView textViewHeader;
    TextView textViewBody;
    LinearLayout contentLayout;
    TextView textViewDone;
    FrameLayout frameLayoutDone;
    RoundedLinearLayout bonusLayout;
    RoundedLinearLayout linearLayoutBody;
    RoundedLinearLayout linearLayoutHeader;
    TextView bonusTextView;

    public EarningsGoalView(Context context, EarningOverview.Goal goal, int cycle, boolean showCompletionCollapsed) {
        super(context);
        init(context, goal, cycle, showCompletionCollapsed);
    }

    protected void init(Context context, EarningOverview.Goal goal, int cycle, boolean showCompletionCollapsed) {
        View view = inflate(context,R.layout.custom_earnings_goal,this);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewBody = view.findViewById(R.id.textViewBody);
        textViewDone = view.findViewById(R.id.textViewDone);
        frameLayoutDone = view.findViewById(R.id.frameLayoutDone);
        contentLayout = view.findViewById(R.id.contentLayout);
        bonusLayout = view.findViewById(R.id.bonusView);
        bonusTextView = view.findViewById(R.id.bonusTextView);
        linearLayoutBody = view.findViewById(R.id.linearLayoutBody);
        linearLayoutHeader = view.findViewById(R.id.linearLayoutHeader);

        if(goal.completed_on!=null){
            DateTime completedDate = new DateTime(goal.completed_on*1000L);
            String date = JodaUtil.format(completedDate,R.string.format_date_dashed,Application.getInstance().getLocale());
            String completedOn = ViewUtil.replaceToken(ViewUtil.getString(R.string.status_done_withdate),R.string.token_date,date);
            textViewDone.setText(completedOn);
        }

        if(showCompletionCollapsed){
            linearLayoutHeader.setRadius(ViewUtil.dpToPx(8));
            linearLayoutBody.setVisibility(GONE);
        }

        if(goal.completed){
            frameLayoutDone.setVisibility(VISIBLE);
            bonusLayout.setStrokeColor(R.color.hintDark);
            bonusLayout.removeStrokeDash();
            bonusLayout.setHorizontalGradient(R.color.hintLight,R.color.hintDark);
            bonusTextView.setTextColor(ViewUtil.getColor(R.color.secondaryDark));
        }

        String bonusString = ViewUtil.getString(goal.completed ? R.string.earnings_bonus_complete : R.string.earnings_bonus_incomplete);
        bonusString = ViewUtil.replaceToken(bonusString,R.string.token_amount,goal.value);
        bonusTextView.setText(Html.fromHtml(bonusString));

        switch (goal.name) {
            case TWENTY_ONE_SESSIONS:
                initTwentyOneSessions(goal);
                break;
            case TWO_A_DAY:
                initTwoADay(goal,cycle);
                break;
            case FOUR_OUT_OF_FOUR:
                initFourOutOfFour(goal);
                break;
        }
    }

    private void initTwentyOneSessions(EarningOverview.Goal goal){
        textViewHeader.setText(ViewUtil.getString(R.string.earnings_21tests_header));
        String body = ViewUtil.getString(R.string.earnings_21tests_body);
        body = ViewUtil.replaceToken(body,R.string.token_amount,goal.value);
        textViewBody.setText(Html.fromHtml(body));
        EarningsTwentyOneSessionsView view = new EarningsTwentyOneSessionsView(getContext());
        view.setProgress(goal.progress);
        contentLayout.addView(view);
    }

    private void initTwoADay(EarningOverview.Goal goal, int cycle){
        textViewHeader.setText(ViewUtil.getString(R.string.earnings_2aday_header));
        String body = ViewUtil.getString(R.string.earnings_2aday_body);
        body = ViewUtil.replaceToken(body,R.string.token_amount,goal.value);
        textViewBody.setText(Html.fromHtml(body));
        contentLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentLayout.addView(new EarningsTwoADayView(getContext(), goal, cycle));
    }

    private void initFourOutOfFour(EarningOverview.Goal goal){
        textViewHeader.setText(ViewUtil.getString(R.string.earnings_4of4_header));
        String body = ViewUtil.getString(R.string.earnings_4of4_body);
        body = ViewUtil.replaceToken(body,R.string.token_amount,goal.value);
        textViewBody.setText(Html.fromHtml(body));

        int dp56 = ViewUtil.dpToPx(56);
        int dp4 = ViewUtil.dpToPx(4);
        contentLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp56));

        for(Integer progress : goal.progress_components){
            CircleProgressView progressView = new CircleProgressView(getContext());
            contentLayout.addView(progressView);

            progressView.setMargin(dp4,0,dp4,0);
            progressView.setStrokeWidth((int)ViewUtil.dpToPx(1.5f));
            progressView.setBaseColor(R.color.secondaryAccent);
            progressView.setCheckmarkColor(R.color.secondaryAccent);
            progressView.setSweepColor(R.color.secondary);
            progressView.setProgress(progress,false);
        }

    }

}
