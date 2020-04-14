package com.healthymedium.arc.core;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import com.healthymedium.arc.paths.questions.QuestionLanguagePreference;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.utilities.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.notifications.NotificationTypes;
import com.healthymedium.arc.study.State;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;

import static android.content.Context.ACTIVITY_SERVICE;

public class DebugDialog extends DialogFragment {

    TextView textViewStatus;
    boolean viewingStatus = true;

    View optionsView;
    View statusView;
    Button button;

    static public void launch(){
        DebugDialog dialog = new DebugDialog();
        dialog.show(NavigationManager.getInstance().getFragmentManager(),DebugDialog.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_debug, container, false);

        TextView textViewClearAppData = v.findViewById(R.id.textViewClearAppData);
        textViewClearAppData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager manager = (ActivityManager)getContext().getSystemService(ACTIVITY_SERVICE);
                boolean cleared = manager.clearApplicationUserData();
                if(!cleared) {
                    Toast.makeText(getContext(),"Failed to clear app data",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView textViewLocale = v.findViewById(R.id.textViewLocale);
        textViewLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                QuestionLanguagePreference screen = new QuestionLanguagePreference();
                NavigationManager.getInstance().removeController();
                NavigationManager.getInstance().open(screen);
            }
        });

        TextView textViewSend = v.findViewById(R.id.textViewSend);
        textViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textViewStatus.getText());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Where to?"));
            }
        });

        textViewStatus = v.findViewById(R.id.textviewStatus);
        textViewStatus.setText(getStatus());

        statusView = v.findViewById(R.id.scrollViewStatus);
        optionsView = v.findViewById(R.id.scrollViewOptions);
        button = v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewingStatus){
                    switchToOptions();
                } else {
                    switchToStatus();
                }
            }
        });

        return v;
    }


    private void switchToOptions() {
        viewingStatus = false;
        button.setEnabled(false);
        optionsView.setVisibility(View.VISIBLE);
        statusView.animate().alpha(0.0f).setDuration(200);
        optionsView.animate().alpha(1.0f).setDuration(300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                statusView.setVisibility(View.INVISIBLE);
                button.setText("View Status");
                button.setEnabled(true);
            }
        },300);
    }

    private void switchToStatus() {
        viewingStatus = true;
        button.setEnabled(false);
        statusView.setVisibility(View.VISIBLE);
        statusView.animate().alpha(1.0f).setDuration(300);
        optionsView.animate().alpha(0.0f).setDuration(200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                optionsView.setVisibility(View.INVISIBLE);
                button.setText("View Debug Options");
                button.setEnabled(true);
            }
        },300);
    }

    private String getStatus() {

        State studyState = Study.getStateMachine().getState();
        Participant participant = Study.getParticipant();

        String status = "";
        status += "localeConfig: " + Application.getInstance().getLocale().getDisplayName() + "\n";
        status += "localePrefs: " + PreferencesManager.getInstance().getString(Locale.TAG_LANGUAGE, "null") + "_" +
                PreferencesManager.getInstance().getString(Locale.TAG_COUNTRY, "null") + "\n\n";
        status += "lifecycle: "+Study.getStateMachine().getLifecycleName(studyState.lifecycle).toLowerCase()+"\n";
        status += "path: "+Study.getStateMachine().getPathName(studyState.currentPath).toLowerCase()+"\n\n";

        status += "cycle: "+participant.getState().currentTestCycle +"\n";
        status += "day: "+participant.getState().currentTestDay+"\n";
        status += "test: "+participant.getState().currentTestSession+"\n";
        status += "\nscheduled tests:\n";
        TestCycle cycle = participant.getCurrentTestCycle();
        if(cycle!=null) {
            Log.e("Test Count",String.valueOf(cycle.getNumberOfTests()));
            for(TestDay day : cycle.getTestDays()){
                status += "\nday "+day.getDayIndex()+"\n";
                for (TestSession session : day.getTestSessions()) {
                    status += session.getScheduledTime().toString("MM/dd/yyyy   hh:mm:ss a\n");
                }
            }

        }

        if(Study.getCurrentTestCycle() != null) {
            // Get current visit
            int currVisitId = Study.getCurrentTestCycle().getId();

            // Notification one month before next visit
            NotificationNode month = NotificationManager.getInstance().getNode(NotificationTypes.VisitNextMonth.getId(), currVisitId);
            if (month != null) {
                status += "month notification: " + month.time + "\n";
            }

            // Notification one week before next visit
            NotificationNode week = NotificationManager.getInstance().getNode(NotificationTypes.VisitNextWeek.getId(), currVisitId);
            if (week != null) {
                status += "week notification: " + week.time + "\n";
            }

            // Notification one day before next visit
            NotificationNode day = NotificationManager.getInstance().getNode(NotificationTypes.VisitNextDay.getId(), currVisitId);
            if (day != null) {
                status += "day notification: " + day.time + "\n";
            }
        }
        else {
            status += " -- uninitialized -- \n";
        }
        return status;
    }

}
