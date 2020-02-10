package com.healthymedium.arc.core;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.utilities.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.notifications.NotificationTypes;
import com.healthymedium.arc.study.State;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.navigation.NavigationManager;

public class DebugDialog extends DialogFragment {

    Button button;
    TextView textViewSend;
    TextView textView;

    OnDialogDismiss listener;

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
        button = v.findViewById(R.id.buttonDebugDialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        textViewSend = v.findViewById(R.id.textViewSend);
        textViewSend.setPaintFlags(textViewSend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textView.getText());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Where to?"));
            }
        });

        State studyState = Study.getStateMachine().getState();
        Participant participant = Study.getParticipant();

        String status = "lifecycle: "+Study.getStateMachine().getLifecycleName(studyState.lifecycle).toLowerCase()+"\n";
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


        textView = v.findViewById(R.id.textviewState);
        textView.setText(status);

        return v;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss(String time);
    }

}
