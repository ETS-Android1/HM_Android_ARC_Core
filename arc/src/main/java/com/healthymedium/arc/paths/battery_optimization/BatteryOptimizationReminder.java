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
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.SplashScreen;
import com.healthymedium.arc.font.FontFactory;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.notifications.ProctorDeviation;
import com.healthymedium.arc.paths.informative.HelpScreen;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class BatteryOptimizationReminder extends BaseFragment {

    boolean requested = false;
    Button button;

    public BatteryOptimizationReminder() {
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
        View view = inflater.inflate(R.layout.fragment_battery_optimization, container, false);

        // in case someone experiences a deviation and turns off optimizations before opening the app
        // however unlikely that is
        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if(powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName())){

            ProctorDeviation deviation = ProctorDeviation.load();
            deviation.markRequest();
            deviation.save();

            NavigationManager.getInstance().open(new SplashScreen());
        }

        TextView textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoMedium);

        TextView textViewHelp = view.findViewById(R.id.textViewHelp);
        textViewHelp.setTypeface(Fonts.robotoMedium);
        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpScreen help = new HelpScreen();
                NavigationManager.getInstance().open(help);
            }
        });

        TextView textViewBody = view.findViewById(R.id.textViewBody);
        textViewBody.setText("We've noticed that some notifications aren't showing at the correct time.\n\nTo make sure notifications show up at the correct time, you can turn off the battery optimizations for this app.");

        button = view.findViewById(R.id.button);
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

        TextView textViewProceed = view.findViewById(R.id.textViewProceed);
        ViewUtil.underlineTextView(textViewProceed);
        ViewUtil.setLineHeight(textViewProceed,24);
        textViewProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().open(new SplashScreen());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(requested) {
            NavigationManager.getInstance().open(new SplashScreen());
        }
    }

}
