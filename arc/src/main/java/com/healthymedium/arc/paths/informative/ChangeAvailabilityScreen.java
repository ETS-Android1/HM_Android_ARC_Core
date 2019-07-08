package com.healthymedium.arc.paths.informative;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Visit;
import com.healthymedium.arc.utilities.NavigationManager;

import org.joda.time.DateTime;

public class ChangeAvailabilityScreen extends BaseFragment {

    String stringHeader;
    String changeDateHeader;

    boolean showChangeDate;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewChangeDate;

    Button button;
    Button changeDateButton;

    FrameLayout lineFrameLayout;

    public ChangeAvailabilityScreen() {
        stringHeader = Application.getInstance().getResources().getString(R.string.ChangeAvail_time);

        Participant participant = Study.getParticipant();
        Visit visit = participant.getCurrentVisit();
        DateTime startDate = visit.getScheduledStartDate();
        DateTime endDate = visit.getScheduledEndDate();
        DateTime now = DateTime.now();

        if (now.isBefore(startDate) || now.isAfter(endDate)) {
            // We're not in a visit, so show the 1 week adjustment option
            showChangeDate = true;
            changeDateHeader = Application.getInstance().getResources().getString(R.string.ChangeAvail_date);
        } else {
            // We are in a visit
            showChangeDate = false;
        }

        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_availability, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewChangeDate = view.findViewById(R.id.textViewChangeDate);
        changeDateButton = view.findViewById(R.id.changeDateButton);

        lineFrameLayout = view.findViewById(R.id.lineFrameLayout);

        if (showChangeDate) {
            textViewChangeDate.setText(Html.fromHtml(changeDateHeader));
            changeDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Study.adjustSchedule();
//                    Participant participant = Study.getParticipant();
//                    Visit visit = participant.getCurrentVisit();
//                    for (int i = 0; i < visit.testSessions.size() ; i++) {
//                        TestSession temp = visit.testSessions.get(i);
//                        temp.setScheduledTime(temp.getScheduledTime().plusWeeks(1));
//                        visit.testSessions.set(i, temp);
//                    }
                }
            });
        } else {
            textViewChangeDate.setVisibility(View.GONE);
            changeDateButton.setVisibility(View.GONE);
            lineFrameLayout.setVisibility(View.GONE);
        }

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });


        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String number = stringPhoneNumber.replace("-","");
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
//                startActivity(intent);
                Study.updateAvailability(8, 18);
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        return view;
    }

}
