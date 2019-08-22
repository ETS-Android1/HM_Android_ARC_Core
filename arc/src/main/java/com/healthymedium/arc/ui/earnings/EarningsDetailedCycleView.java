package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.ui.base.RoundedFrameLayout;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.ui.base.RoundedRelativeLayout;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import static com.healthymedium.arc.study.Earnings.FOUR_OUT_OF_FOUR;
import static com.healthymedium.arc.study.Earnings.TEST_SESSION;
import static com.healthymedium.arc.study.Earnings.TWENTY_ONE_SESSIONS;
import static com.healthymedium.arc.study.Earnings.TWO_A_DAY;

public class EarningsDetailedCycleView extends LinearLayout {

    TextView textViewTitle;
    TextView textViewDates;

    LinearLayout goalLayout;
    RoundedFrameLayout ongoingLayout;

    TextView cycleTotal;


    public EarningsDetailedCycleView(Context context, EarningDetails.Cycle cycle) {
        super(context);
        View view = inflate(context, R.layout.custom_earnings_cycle_details,this);

        DateTime start = new DateTime(cycle.start_date*1000L);
        DateTime end = new DateTime(cycle.end_date*1000L);

        boolean isCurrent = (end.isAfterNow()&&start.isBeforeNow());

        textViewTitle = view.findViewById(R.id.textViewTitle);
        if(isCurrent){
            textViewTitle.setText(ViewUtil.getString(R.string.earnings_details_subheader1));
            ongoingLayout = view.findViewById(R.id.ongoingLayout);
            ongoingLayout.setVisibility(VISIBLE);
        }

        String startString = JodaUtil.format(start,R.string.format_date_lo);
        String endString = JodaUtil.format(end,R.string.format_date_lo);
        String dates = ViewUtil.getString(R.string.earnings_details_dates);
        dates = ViewUtil.replaceToken(dates,R.string.token_date1,startString);
        dates = ViewUtil.replaceToken(dates,R.string.token_date2,endString);

        textViewDates = view.findViewById(R.id.textViewDates);
        textViewDates.setText(dates);

        goalLayout = view.findViewById(R.id.goalLayout);
        boolean highlight = false;

        for(EarningDetails.Goal goal : cycle.details) {
            EarningsDetailedGoalView goalView = new EarningsDetailedGoalView(context,goal);
            highlight = !highlight;
            if(highlight){
                goalView.highlight();
            }
            goalLayout.addView(goalView);
        }

        cycleTotal = view.findViewById(R.id.cycleTotal);
        cycleTotal.setText(cycle.total);

    }

}
