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

        DateTime visitStart = visit.getActualStartDate();
        DateTime visitEnd = visit.getActualEndDate();

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

        fmt = DateTimeFormat.forPattern("d");

        if (startDay.equals("Mon")) {
            // start
            monday1.setText(fmt.print(startTime));

            // end
            sunday2.setText(fmt.print(startTime.plusDays(6)));

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(1)));
            tuesday1.setText(fmt.print(startTime.plusDays(1)));
            wednesday1.setText(fmt.print(startTime.plusDays(2)));
            thursday1.setText(fmt.print(startTime.plusDays(3)));
            friday1.setText(fmt.print(startTime.plusDays(4)));
            saturday1.setText(fmt.print(startTime.plusDays(5)));
            monday2.setText(fmt.print(startTime.plusDays(7)));
            tuesday2.setText(fmt.print(startTime.plusDays(8)));
            wednesday2.setText(fmt.print(startTime.plusDays(9)));
            thursday2.setText(fmt.print(startTime.plusDays(10)));
            friday2.setText(fmt.print(startTime.plusDays(11)));
            saturday2.setText(fmt.print(startTime.plusDays(12)));

            // update backgrounds
            monday1.setBackgroundResource(R.color.primary);
            tuesday1.setBackgroundResource(R.color.primary);
            wednesday1.setBackgroundResource(R.color.primary);
            thursday1.setBackgroundResource(R.color.primary);
            friday1.setBackgroundResource(R.color.primary);
            saturday1.setBackgroundResource(R.color.primary);
            sunday2.setBackgroundResource(R.color.primary);

        } else if (startDay.equals("Tue")) {
            // start
            tuesday1.setText(fmt.print(startTime));

            // end
            monday2.setText(fmt.print(startTime.plusDays(6)));

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(2)));
            monday1.setText(fmt.print(startTime.minusDays(1)));
            wednesday1.setText(fmt.print(startTime.plusDays(1)));
            thursday1.setText(fmt.print(startTime.plusDays(2)));
            friday1.setText(fmt.print(startTime.plusDays(3)));
            saturday1.setText(fmt.print(startTime.plusDays(4)));
            sunday2.setText(fmt.print(startTime.plusDays(5)));
            tuesday2.setText(fmt.print(startTime.plusDays(7)));
            wednesday2.setText(fmt.print(startTime.plusDays(8)));
            thursday2.setText(fmt.print(startTime.plusDays(9)));
            friday2.setText(fmt.print(startTime.plusDays(10)));
            saturday2.setText(fmt.print(startTime.plusDays(11)));

            // update backgrounds
            tuesday1.setBackgroundResource(R.color.primary);
            wednesday1.setBackgroundResource(R.color.primary);
            thursday1.setBackgroundResource(R.color.primary);
            friday1.setBackgroundResource(R.color.primary);
            saturday1.setBackgroundResource(R.color.primary);
            sunday2.setBackgroundResource(R.color.primary);
            monday2.setBackgroundResource(R.color.primary);

        } else if (startDay.equals("Wed")) {
            // start
            wednesday1.setText(fmt.print(startTime));

            // end
            tuesday2.setText(fmt.print(startTime.plusDays(6)));

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(3)));
            monday1.setText(fmt.print(startTime.minusDays(2)));
            tuesday1.setText(fmt.print(startTime.minusDays(1)));
            thursday1.setText(fmt.print(startTime.plusDays(1)));
            friday1.setText(fmt.print(startTime.plusDays(2)));
            saturday1.setText(fmt.print(startTime.plusDays(3)));
            sunday2.setText(fmt.print(startTime.plusDays(4)));
            monday2.setText(fmt.print(startTime.plusDays(5)));
            wednesday2.setText(fmt.print(startTime.plusDays(7)));
            thursday2.setText(fmt.print(startTime.plusDays(8)));
            friday2.setText(fmt.print(startTime.plusDays(9)));
            saturday2.setText(fmt.print(startTime.plusDays(10)));

            // update backgrounds
            wednesday1.setBackgroundResource(R.color.primary);
            thursday1.setBackgroundResource(R.color.primary);
            friday1.setBackgroundResource(R.color.primary);
            saturday1.setBackgroundResource(R.color.primary);
            sunday2.setBackgroundResource(R.color.primary);
            monday2.setBackgroundResource(R.color.primary);
            tuesday2.setBackgroundResource(R.color.primary);

        } else if (startDay.equals("Thur")) {
            // start
            thursday1.setText(fmt.print(startTime));

            // end
            wednesday2.setText(fmt.print(startTime.plusDays(6)));

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(4)));
            monday1.setText(fmt.print(startTime.minusDays(3)));
            tuesday1.setText(fmt.print(startTime.minusDays(2)));
            wednesday1.setText(fmt.print(startTime.minusDays(1)));
            friday1.setText(fmt.print(startTime.plusDays(1)));
            saturday1.setText(fmt.print(startTime.plusDays(2)));
            sunday2.setText(fmt.print(startTime.plusDays(3)));
            monday2.setText(fmt.print(startTime.plusDays(4)));
            tuesday2.setText(fmt.print(startTime.plusDays(5)));
            thursday2.setText(fmt.print(startTime.plusDays(7)));
            friday2.setText(fmt.print(startTime.plusDays(8)));
            saturday2.setText(fmt.print(startTime.plusDays(9)));

            // update backgrounds
            thursday1.setBackgroundResource(R.color.primary);
            friday1.setBackgroundResource(R.color.primary);
            saturday1.setBackgroundResource(R.color.primary);
            sunday2.setBackgroundResource(R.color.primary);
            monday2.setBackgroundResource(R.color.primary);
            tuesday2.setBackgroundResource(R.color.primary);
            wednesday2.setBackgroundResource(R.color.primary);

        } else if (startDay.equals("Fri")) {
            // start
            friday1.setText(fmt.print(startTime));

            // end
            thursday2.setText(fmt.print(startTime.plusDays(6)));

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(5)));
            monday1.setText(fmt.print(startTime.minusDays(4)));
            tuesday1.setText(fmt.print(startTime.minusDays(3)));
            wednesday1.setText(fmt.print(startTime.minusDays(2)));
            thursday1.setText(fmt.print(startTime.minusDays(1)));
            saturday1.setText(fmt.print(startTime.plusDays(1)));
            sunday2.setText(fmt.print(startTime.plusDays(2)));
            monday2.setText(fmt.print(startTime.plusDays(3)));
            tuesday2.setText(fmt.print(startTime.plusDays(4)));
            wednesday2.setText(fmt.print(startTime.plusDays(5)));
            friday2.setText(fmt.print(startTime.plusDays(7)));
            saturday2.setText(fmt.print(startTime.plusDays(8)));

            // update backgrounds
            friday1.setBackgroundResource(R.color.primary);
            saturday1.setBackgroundResource(R.color.primary);
            sunday2.setBackgroundResource(R.color.primary);
            monday2.setBackgroundResource(R.color.primary);
            tuesday2.setBackgroundResource(R.color.primary);
            wednesday2.setBackgroundResource(R.color.primary);
            thursday2.setBackgroundResource(R.color.primary);

        } else if (startDay.equals("Sat")) {
            // start
            saturday1.setText(fmt.print(startTime));

            // end
            friday2.setText(fmt.print(startTime.plusDays(6)));

            // everything else
            sunday1.setText(fmt.print(startTime.minusDays(6)));
            monday1.setText(fmt.print(startTime.minusDays(5)));
            tuesday1.setText(fmt.print(startTime.minusDays(4)));
            wednesday1.setText(fmt.print(startTime.minusDays(3)));
            thursday1.setText(fmt.print(startTime.minusDays(2)));
            friday1.setText(fmt.print(startTime.minusDays(1)));
            sunday2.setText(fmt.print(startTime.plusDays(1)));
            monday2.setText(fmt.print(startTime.plusDays(2)));
            tuesday2.setText(fmt.print(startTime.plusDays(3)));
            wednesday2.setText(fmt.print(startTime.plusDays(4)));
            thursday2.setText(fmt.print(startTime.plusDays(5)));
            saturday2.setText(fmt.print(startTime.plusDays(7)));

            // update backgrounds
            saturday1.setBackgroundResource(R.color.primary);
            sunday2.setBackgroundResource(R.color.primary);
            monday2.setBackgroundResource(R.color.primary);
            tuesday2.setBackgroundResource(R.color.primary);
            wednesday2.setBackgroundResource(R.color.primary);
            thursday2.setBackgroundResource(R.color.primary);
            friday2.setBackgroundResource(R.color.primary);

        } else if (startDay.equals("Sun")) {
            // start
            sunday1.setText(fmt.print(startTime));

            // end
            saturday1.setText(fmt.print(startTime.plusDays(6)));

            // everything else
            monday1.setText(fmt.print(startTime.plusDays(1)));
            tuesday1.setText(fmt.print(startTime.plusDays(2)));
            wednesday1.setText(fmt.print(startTime.plusDays(3)));
            thursday1.setText(fmt.print(startTime.plusDays(4)));
            friday1.setText(fmt.print(startTime.plusDays(5)));
            sunday2.setText(fmt.print(startTime.plusDays(7)));
            monday2.setText(fmt.print(startTime.plusDays(8)));
            tuesday2.setText(fmt.print(startTime.plusDays(9)));
            wednesday2.setText(fmt.print(startTime.plusDays(10)));
            thursday2.setText(fmt.print(startTime.plusDays(11)));
            friday2.setText(fmt.print(startTime.plusDays(12)));
            saturday2.setText(fmt.print(startTime.plusDays(13)));

            // update backgrounds
            sunday1.setBackgroundResource(R.color.primary);
            monday1.setBackgroundResource(R.color.primary);
            tuesday1.setBackgroundResource(R.color.primary);
            wednesday1.setBackgroundResource(R.color.primary);
            thursday1.setBackgroundResource(R.color.primary);
            friday1.setBackgroundResource(R.color.primary);
            saturday1.setBackgroundResource(R.color.primary);
        }
    }
}
