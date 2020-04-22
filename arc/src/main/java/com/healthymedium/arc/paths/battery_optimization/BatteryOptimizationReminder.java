package com.healthymedium.arc.paths.battery_optimization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.font.FontFactory;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.notifications.ProctorDeviation;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class BatteryOptimizationReminder extends StateInfoTemplate {

    boolean requested = false;
    String body = "Your notifications are experiencing a delay. Next, there will be a pop-up asking you to turn off battery optimization by allowing the {APP_NAME} app to run in the background. Please select “<b>allow</b>” so we know your notifications are working correctly.";

    public BatteryOptimizationReminder() {
        super(false,
                "Battery Optimization",
                null,
                "",
                ViewUtil.getString(R.string.button_next)
        );

        if(FontFactory.getInstance()==null) {
            FontFactory.initialize(Application.getInstance());
        }

        if(!Fonts.areLoaded()){
            Fonts.load();
            FontFactory.getInstance().setDefaultFont(Fonts.roboto);
            FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        // in case someone experiences a deviation and turns off optimizations before opening the app
        // however unlikely that is
        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if(powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName())){

            ProctorDeviation deviation = ProctorDeviation.load();
            deviation.markRequest();
            deviation.save();

            NavigationManager.getInstance().popBackStack();
            NavigationManager.getInstance().open(new SplashScreen());
        }

        TextView textViewBody = view.findViewById(R.id.textViewBody);

        Phrase phrase = new Phrase(body);
        phrase.replace(R.string.token_app_name,R.string.app_name);
        textViewBody.setText(phrase.toHtmlString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                requested = true;

                ProctorDeviation deviation = ProctorDeviation.load();
                deviation.markRequest();
                deviation.save();

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(requested) {
            NavigationManager.getInstance().popBackStack();
            NavigationManager.getInstance().open(new SplashScreen());
        }
    }

}
