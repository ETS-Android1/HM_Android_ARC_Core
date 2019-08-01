package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;

public class EarningsGoalView extends RoundedLinearLayout {

    TextView textViewHeader;
    TextView textViewBody;
    LinearLayout contentLayout;
    EarningsBonusView bonusView;

    public EarningsGoalView(Context context) {
        super(context);
        init(context, null);
    }

    public EarningsGoalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EarningsGoalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        View view = inflate(context,R.layout.custom_earnings_goal,this);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewBody = view.findViewById(R.id.textViewBody);
        contentLayout = view.findViewById(R.id.contentLayout);
        bonusView = view.findViewById(R.id.bonusView);

    }

    public void setHeaderText(String text){
        textViewHeader.setText(text);
    }

    public void setBodyText(String text){
        textViewBody.setText(text);
    }

    public void setBonusText(String text){
        bonusView.setTextCenter(text);
    }

}
