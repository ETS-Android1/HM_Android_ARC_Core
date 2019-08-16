package com.healthymedium.arc.paths.notification;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationUtil;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class NotificationOverview extends StateInfoTemplate {

    public NotificationOverview() {
        super(false,
                ViewUtil.getString(R.string.onboarding_notifications_header1),
                null,
                ViewUtil.getString(R.string.onboarding_notifications_body1),
                ViewUtil.getString(R.string.button_next)
                );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                if(NotificationUtil.areNotificationsEnabled(getContext())){
                    Study.getParticipant().markShownNotificationOverview();
                    Study.openNextFragment();
                    Study.openNextFragment();
                } else {
                    Study.openNextFragment();
                }
            }
        });

        return view;
    }

}
