package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class EarningsGoalView extends RoundedLinearLayout {

    static final String TWENTY_ONE_SESSIONS = "21-sessions";
    static final String TWO_A_DAY = "2-a-day";
    static final String FOUR_OUT_OF_FOUR = "4-out-of-4";

    TextView textViewHeader;
    TextView textViewBody;
    LinearLayout contentLayout;
    FrameLayout frameLayoutDone;
    EarningsBonusView bonusView;

    public EarningsGoalView(Context context, EarningOverview.Goals.Goal goal) {
        super(context);
        init(context, goal);
    }

    protected void init(Context context, EarningOverview.Goals.Goal goal) {
        View view = inflate(context,R.layout.custom_earnings_goal,this);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewBody = view.findViewById(R.id.textViewBody);
        frameLayoutDone = view.findViewById(R.id.frameLayoutDone);
        contentLayout = view.findViewById(R.id.contentLayout);
        bonusView = view.findViewById(R.id.bonusView);

        bonusView.setUnearned(!goal.completed);

        String bonusString = ViewUtil.getString(goal.completed ? R.string.earnings_bonus_complete : R.string.earnings_bonus_incomplete);
        bonusString = ViewUtil.replaceToken(bonusString,R.string.token_amount,goal.value);
        bonusView.setTextCenter(bonusString);

        if(!goal.completed){
            frameLayoutDone.setVisibility(GONE);
        }

        switch (goal.name) {
            case TWENTY_ONE_SESSIONS:
                initTwentyOneSessions(goal);
                break;
            case TWO_A_DAY:
                initTwoADay(goal);
                break;
            case FOUR_OUT_OF_FOUR:
                initFourOutOfFour(goal);
                break;
        }
    }

    private void initTwentyOneSessions(EarningOverview.Goals.Goal goal){
        textViewHeader.setText(ViewUtil.getString(R.string.earnings_21tests_header));
        String body = ViewUtil.getString(R.string.earnings_21tests_body);
        body = ViewUtil.replaceToken(body,R.string.token_amount,goal.value);
        textViewBody.setText(Html.fromHtml(body));
//        contentLayout.addView();
    }

    private void initTwoADay(EarningOverview.Goals.Goal goal){
        textViewHeader.setText(ViewUtil.getString(R.string.earnings_2aday_header));
        String body = ViewUtil.getString(R.string.earnings_2aday_body);
        body = ViewUtil.replaceToken(body,R.string.token_amount,goal.value);
        textViewBody.setText(Html.fromHtml(body));
//        contentLayout.addView();
    }

    private void initFourOutOfFour(EarningOverview.Goals.Goal goal){
        textViewHeader.setText(ViewUtil.getString(R.string.earnings_4of4_header));
        String body = ViewUtil.getString(R.string.earnings_4of4_body);
        body = ViewUtil.replaceToken(body,R.string.token_amount,goal.value);
        textViewBody.setText(Html.fromHtml(body));
//        contentLayout.addView();
    }

}
