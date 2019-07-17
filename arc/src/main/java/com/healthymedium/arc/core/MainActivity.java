package com.healthymedium.arc.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.healthymedium.arc.custom.BottomNavigationView;
import com.healthymedium.arc.utilities.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.questions.QuestionLanguagePreference;
import com.healthymedium.arc.study.AbandonmentJobService;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.HomeWatcher;
import com.healthymedium.arc.utilities.KeyboardWatcher;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean paused = false;
    boolean backAllowed = false;
    boolean backInStudy = false;
    boolean hasNewIntent = false;
    int backInStudySkips = 0;

    int lastNavigationBarItem = R.id.menu_home;

    boolean checkAbandonment = false;

    FrameLayout contentView;
    HomeWatcher homeWatcher;
    KeyboardWatcher keyboardWatcher;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onStart() {
        super.onStart();
        if(hasNewIntent){
            hasNewIntent = false;
            if(!Study.getStateMachine().isCurrentlyInTestPath()){
                Study.openNextFragment();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(new Bundle());
        Log.i("MainActivity","onCreate");

        Intent intent = getIntent();
        parseIntent(intent);

        setContentView(R.layout.core_activity_main);
        contentView = findViewById(R.id.content_frame);

        bottomNavigationView = findViewById(R.id.navigation);

        setup();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("MainActivity","onNewIntent");
        parseIntent(intent);
        hasNewIntent = true;

    }

    private void parseIntent(Intent intent){
        Log.i("MainActivity","parseIntent");
        if(intent!=null) {
            Config.OPENED_FROM_NOTIFICATION = intent.getBooleanExtra(Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION,false);
            Config.OPENED_FROM_VISIT_NOTIFICATION = intent.getBooleanExtra(Config.INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION,false);
        }
        Log.i("MainActivity","OPENED_FROM_NOTIFICATION = "+Config.OPENED_FROM_NOTIFICATION);
        Log.i("MainActivity","OPENED_FROM_VISIT_NOTIFICATION = "+Config.OPENED_FROM_VISIT_NOTIFICATION);
    }

    public void setup(){
        NavigationManager.initializeInstance(getSupportFragmentManager());
        if(PreferencesManager.getInstance().contains(Locale.TAG_LANGUAGE) || !Config.CHOOSE_LOCALE){
            NavigationManager.getInstance().open(new SplashScreen());
            return;
        }

        List<Locale> locales = Application.getInstance().getLocaleOptions();
        List<String> options = new ArrayList<>();
        for(Locale locale : locales) {
            options.add(locale.getLabel());
        }
        QuestionLanguagePreference fragment = new QuestionLanguagePreference(
                false,
                true,
                "Language:",
                "",
                options,
                locales,
                "CONFIRM");
        NavigationManager.getInstance().open(fragment);
    }

    public void setupHomeWatcher(){
        homeWatcher = new HomeWatcher(this);
        homeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                paused = true;
                checkAbandonment = false;
                if(Study.isValid()){

                    if(Study.getInstance().getParticipant().isCurrentlyInTestSession()){
                        checkAbandonment = true;
                    }
                }
            }
            @Override
            public void onHomeLongPressed() {

            }
        });
        homeWatcher.startWatch();
    }

    public void setupKeyboardWatcher(){
        keyboardWatcher = new KeyboardWatcher(this);
        keyboardWatcher.startWatch();
    }

    public void setKeyboardListener(KeyboardWatcher.OnKeyboardToggleListener listener){
        keyboardWatcher.setListener(listener);
    }

    public void removeKeyboardListener(){
        keyboardWatcher.setListener(null);
    }


    public void enableBackPress(boolean enable, boolean inStudy, int skips){
        Log.i("MainActivity","enableBackPress(enable="+enable+", inStudy="+inStudy+")");
        backInStudySkips = skips;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    public void enableBackPress(boolean enable, boolean inStudy){
        Log.i("MainActivity","enableBackPress(enable="+enable+", inStudy="+inStudy+")");
        backInStudySkips = 0;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            /*
            if(homeWatcher != null){
                homeWatcher.stopWatch();
                homeWatcher = null;
            }
            finish();
            */
        } else if(backAllowed){
            if(Study.isValid() && backInStudy){
                Study.openPreviousFragment(backInStudySkips);
            } else {
                NavigationManager.getInstance().popBackStack();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("MainActivity","onDestroy");
        if(homeWatcher != null){
            homeWatcher.stopWatch();
            homeWatcher = null;
        }
        if(keyboardWatcher != null){
            keyboardWatcher.stopWatch();
            keyboardWatcher = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Study.isValid()){
            AbandonmentJobService.unscheduleSelf(getApplicationContext());
            //if we were paused, then we need to call the resume handler.
            if(paused) {
                Study.getParticipant().markResumed();
            }
        }
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        if(Study.isValid()){
            Study.getParticipant().markPaused();
            Study.getStateMachine().save(true);
            if(Study.getParticipant().isCurrentlyInTestSession()) {
                AbandonmentJobService.scheduleSelf(getApplicationContext());
            }
        }
    }


    @Override
    protected void attachBaseContext(Context context) {
        Log.i("MainActivity","attachBaseContext");
        super.attachBaseContext(context);
        Application.getInstance().updateLocale(context);
    }

    public boolean isVisible() {
        return !paused;
    }

    public View getContentView(){
        return contentView;
    }

    public void showNavigationBar() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void hideNavigationBar() {
        bottomNavigationView.setVisibility(View.GONE);
    }

}
