package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedRelativeLayout;
import com.healthymedium.arc.utilities.ViewUtil;

import static com.healthymedium.arc.api.models.EarningOverview.FOUR_OUT_OF_FOUR;
import static com.healthymedium.arc.api.models.EarningOverview.TEST_SESSION;
import static com.healthymedium.arc.api.models.EarningOverview.TWENTY_ONE_SESSIONS;
import static com.healthymedium.arc.api.models.EarningOverview.TWO_A_DAY;

public class EarningsAchievementView extends RoundedRelativeLayout {

    TextView textViewName;
    TextView textViewValue;

    public EarningsAchievementView(Context context, EarningOverview.Achievement achievement) {
        super(context);
        View view = inflate(context, R.layout.custom_earnings_achievement,this);

        textViewName = view.findViewById(R.id.textViewName);
        String name = new String();
        switch (achievement.name) {
            case TEST_SESSION:
                name = ViewUtil.getString(R.string.progress_earnings_status1);
                break;
            case TWENTY_ONE_SESSIONS:
                name = ViewUtil.getString(R.string.progress_earnings_status2);
                break;
            case TWO_A_DAY:
                name = ViewUtil.getString(R.string.progress_earnings_status4);
                break;
            case FOUR_OUT_OF_FOUR:
                name = ViewUtil.getString(R.string.progress_earnings_status3);
                break;
        }
        textViewName.setText(name);

        textViewValue = view.findViewById(R.id.textViewValue);
        textViewValue.setText(ViewUtil.getString(R.string.earnings_symbol)+achievement.amount_earned);
    }

}
