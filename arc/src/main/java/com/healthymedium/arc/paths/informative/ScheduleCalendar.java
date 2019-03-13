package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Visit;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;
import com.healthymedium.arc.custom.Button;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ScheduleCalendar extends BaseFragment {

    View view;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubHeader;
    Button buttonNext;

    public ScheduleCalendar() {
        allowBackPress(true);
        setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
        setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_calendar, container, false);


        Participant participant = Study.getParticipant();
        Visit visit = participant.getCurrentVisit();

        DateTime visitStart = visit.getScheduledStartDate();
        DateTime visitEnd = visit.getScheduledEndDate();

        DateTimeFormatter fmt = DateTimeFormat.forPattern("E, MMM d");

        String start = fmt.print(visitStart);
        String end = fmt.print(visitEnd);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml("Great! Your next testing cycle will be <b>" + start + "</b> through <b>" + end + "</b>."));

        updateCalendar(visitStart, visitEnd);

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        //textViewSubHeader.setLineSpacing(ViewUtil.dpToPx(3),1.0f);

        textViewSubHeader.setVisibility(View.GONE);

        textViewBack = view.findViewById(R.id.textViewBack);
        //textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        textViewBack.setVisibility(View.VISIBLE);


        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openNextFragment();
            }
        });

        return view;
    }

    public void updateCalendar(DateTime startTime, DateTime endTime) {

        // Get calendar components
        TextView sunday1 = view.findViewById(R.id.sunday1);
        TextView sunday2 = view.findViewById(R.id.sunday2);

        TextView monday1 = view.findViewById(R.id.monday1);
        TextView monday2 = view.findViewById(R.id.monday2);

        TextView tuesday1 = view.findViewById(R.id.tuesday1);
        TextView tuesday2 = view.findViewById(R.id.tuesday2);

        TextView wednesday1 = view.findViewById(R.id.wednesday1);
        TextView wednesday2 = view.findViewById(R.id.wednesday2);

        TextView thursday1 = view.findViewById(R.id.thursday1);
        TextView thursday2 = view.findViewById(R.id.thursday2);

        TextView friday1 = view.findViewById(R.id.friday1);
        TextView friday2 = view.findViewById(R.id.friday2);

        TextView saturday1 = view.findViewById(R.id.saturday1);
        TextView saturday2 = view.findViewById(R.id.saturday2);



        DateTimeFormatter fmt = DateTimeFormat.forPattern("E");
        String startDay = fmt.print(startTime);
        String endDay = fmt.print(endTime);

        fmt = DateTimeFormat.forPattern("d");
        String startDayNum = fmt.print(startTime);

        if (startDay.equals("Mon")) {
            // start
            monday1.setText(startDayNum);

            // end
            sunday2.setText(Integer.toString(Integer.parseInt(startDayNum) + 6));

        } else if (startDay.equals("Tue")) {
            // start
            tuesday1.setText(startDayNum);

            // end
            monday2.setText(Integer.toString(Integer.parseInt(startDayNum) + 6));

        } else if (startDay.equals("Wed")) {
            // start
            wednesday1.setText(startDayNum);

            // end
            tuesday2.setText(Integer.toString(Integer.parseInt(startDayNum) + 6));

        } else if (startDay.equals("Thur")) {
            // start
            thursday1.setText(startDayNum);

            // end
            wednesday2.setText(Integer.toString(Integer.parseInt(startDayNum) + 6));

        } else if (startDay.equals("Fri")) {
            // start
            friday1.setText(startDayNum);

            // end
            thursday2.setText(Integer.toString(Integer.parseInt(startDayNum) + 6));

        } else if (startDay.equals("Sat")) {
            // start
            saturday1.setText(startDayNum);

            // end
            friday2.setText(Integer.toString(Integer.parseInt(startDayNum) + 6));

        } else if (startDay.equals("Sun")) {
            // start
            sunday1.setText(startDayNum);

            // end
            saturday1.setText(Integer.toString(Integer.parseInt(startDayNum) + 6));

        }
    }
}
