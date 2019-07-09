package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.Visit;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

@SuppressLint("ValidFragment")
public class LandingTemplate extends BaseFragment {

    String stringHeader;
    String stringSubheader;
    Boolean boolTestReady;

    protected TextView textViewHeader;
    protected TextView textViewSubheader;
    protected LinearLayout content;
    protected FrameLayout frameLayoutContact;
    protected TextView textViewContact;

    public LandingTemplate(Boolean testReady) {
        boolTestReady = testReady;
        determineStrings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_landing, container, false);
        content = view.findViewById(R.id.linearLayoutContent);
        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewSubheader = view.findViewById(R.id.textViewSubHeader);
        textViewSubheader.setText(Html.fromHtml(stringSubheader));

        if (boolTestReady) {
            Button button = new Button(getContext());
            button.setText(Application.getInstance().getResources().getString(R.string.button_begin));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMainActivity().hideNavigationBar();
                    Study.getCurrentTestSession().markStarted();
                    Study.getParticipant().save();
                    Study.getStateMachine().save();
                    Study.openNextFragment();
                }
            });
            content.addView(button);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = ViewUtil.dpToPx(32);
            params.topMargin = ViewUtil.dpToPx(16);
            content.setLayoutParams(params);
        }

        getMainActivity().showNavigationBar();

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    private void determineStrings() {
        // TODO
        // Bugs with wrong state displaying when a test is available
        // Possibly fix by getting the state from StudyStateMachine

        Participant participant = Study.getParticipant();

        String language = PreferencesManager.getInstance().getString("language", "en");
        String country = PreferencesManager.getInstance().getString("country", "US");
        Locale locale = new Locale(language, country);

//        String header = Application.getInstance().getResources().getString(R.string.home_header2);
//        String body = Application.getInstance().getResources().getString(R.string.home_body2);

        // Default
        String header = Application.getInstance().getResources().getString(R.string.home_header1) + "<br /> <br />" + Application.getInstance().getResources().getString(R.string.home_body1);
        String body = "";

        if (participant.getState().currentVisit != 5) {
            Visit visit = participant.getCurrentVisit();

            DateTime today = new DateTime().withTimeAtStartOfDay();
            DateTime schedStartDateMinusOne = visit.getActualStartDate().minusDays(1);

            // After the cycle, one day before the start of the next session
            if (schedStartDateMinusOne.isEqual(today)) {
                header = Application.getInstance().getResources().getString(R.string.home_header5);

                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);
                DateTime endDate = participant.getCurrentVisit().getActualEndDate();
                String endDateFmt = fmt.print(endDate.minusDays(1));

                header = header.replace("{DATE}", endDateFmt);
                body = Application.getInstance().getResources().getString(R.string.home_body5);
            }
            // After the cycle before the start of the next session
            else if (visit.getNumberOfTestsLeft() == visit.getNumberOfTests()) {
                header = Application.getInstance().getResources().getString(R.string.home_header4);

                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);
                DateTime startDate = participant.getCurrentVisit().getActualStartDate();
                String startDateFmt = fmt.print(startDate);

                DateTime endDate = visit.getActualEndDate();
                String endDateFmt = fmt.print(endDate.minusDays(1));

                body = Application.getInstance().getResources().getString(R.string.home_body4).replace("{DATE1}", startDateFmt);
                body = body.replace("{DATE2}", endDateFmt);
            }
            // After 4th test of the day
            else if (visit.getNumberOfTestsLeftForToday() == 0) {
                header = Application.getInstance().getResources().getString(R.string.home_header3);
                body = Application.getInstance().getResources().getString(R.string.home_body3);
            }
            // Open the app, no test, still in a cycle
            else if (visit.getNumberOfTestsLeft() > 0) {
                header = Application.getInstance().getResources().getString(R.string.home_header2);
                body = Application.getInstance().getResources().getString(R.string.home_body2);
            }
        }
        // No more tests, end of study
        else if (!Study.getParticipant().isStudyRunning()) {
            header = Application.getInstance().getResources().getString(R.string.home_header6);
            body = Application.getInstance().getResources().getString(R.string.home_body6);
        }

        stringHeader = header;
        stringSubheader = body;
    }

}
