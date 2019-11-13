package com.healthymedium.arc.paths.informative;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class AboutScreen extends BaseFragment {

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubHeader;
    Button button3rdParty;

    public AboutScreen() {
        allowBackPress(false);
        setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
        setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setLineSpacing(ViewUtil.dpToPx(3),1.0f);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        Resources res = Application.getInstance().getResources();
        String name = res.getString(R.string.app_name);

        button3rdParty = view.findViewById(R.id.button3rdParty);

        if (!name.equals("TU ARC") && !name.equals("TU ARC (QA)") && !name.equals("TU ARC (DEV)")) {
            button3rdParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ThirdPartyMaterialsScreen thirdPartyScreen = new ThirdPartyMaterialsScreen();
                    NavigationManager.getInstance().open(thirdPartyScreen);
                }
            });
        } else {
            button3rdParty.setVisibility(View.GONE);
        }

        return view;
    }

}
