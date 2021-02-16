package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;

public class EarningsPostTestUnavailableScreen extends BaseFragment {

    TextView textView;
    Button button;

    public EarningsPostTestUnavailableScreen() {
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_post_test_unavailable, container, false);

        textView = view.findViewById(R.id.textView);
        button = view.findViewById(R.id.buttonNext);
        button.setAlpha(0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Study.openNextFragment();
            }
        });

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        animateAlpha(1.0f);
    }

    @Override
    protected void onExitTransitionStart(boolean popped) {
        super.onEnterTransitionEnd(popped);
        animateAlpha(0f);
    }

    private void animateAlpha(float value) {
        if(textView!=null){
            textView.animate().alpha(value);
        }
        if (button != null) {
            button.animate().alpha(value);
        }
    }

}
