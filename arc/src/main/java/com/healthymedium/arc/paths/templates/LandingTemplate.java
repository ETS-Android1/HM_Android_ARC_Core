package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.healthymedium.arc.ui.BottomNavigationView;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

@SuppressLint("ValidFragment")
public class LandingTemplate extends BaseFragment {

    protected static final String HINT_FIRST_TEST = "HINT_FIRST_TEST";
    protected static final String HINT_POST_BASELINE = "HINT_POST_BASELINE";

    String stringHeader;
    String stringSubheader;
    Boolean boolTestReady;

    protected LinearLayout landing_layout;
    protected TextView textViewHeader;
    protected TextView textViewSubheader;
    protected LinearLayout content;
    protected FrameLayout frameLayoutContact;
    protected TextView textViewContact;

    HintPointer beginTestHint;
    HintHighlighter beginTestHighlight;

    public LandingTemplate(Boolean testReady) {
        boolTestReady = testReady;
        determineStrings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_landing, container, false);

        landing_layout = view.findViewById(R.id.landing_layout);

        content = view.findViewById(R.id.linearLayoutContent);
        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewSubheader = view.findViewById(R.id.textViewSubHeader);
        textViewSubheader.setText(Html.fromHtml(stringSubheader));

        if (!Hints.hasBeenShown(HINT_POST_BASELINE) && Hints.hasBeenShown(HINT_FIRST_TEST)) {
            showPostBaselineHints();
        }

        if (boolTestReady) {
            Button button = new Button(getContext());
            button.setText(Application.getInstance().getResources().getString(R.string.button_begin));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Hints.hasBeenShown(HINT_FIRST_TEST)) {
                        beginTestHighlight.dismiss();
                        beginTestHint.dismiss();
                        Hints.markShown(HINT_FIRST_TEST);
                    }

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

            if (!Hints.hasBeenShown(HINT_FIRST_TEST)) {
                beginTestHint = new HintPointer(getActivity(), landing_layout, true, false);
                beginTestHint.setText(ViewUtil.getString(R.string.popup_begin));
                beginTestHighlight = new HintHighlighter(getActivity());
                beginTestHighlight.addTarget(landing_layout, 10);

                beginTestHighlight.show();
                beginTestHint.show();
            }
        }

        getMainActivity().showNavigationBar();

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
    }

    private void determineStrings() {
        // TODO
        // Bugs with wrong state displaying when a test is available
        // Possibly fix by getting the state from StudyStateMachine

        Participant participant = Study.getParticipant();

        String language = PreferencesManager.getInstance().getString("language", "en");
        String country = PreferencesManager.getInstance().getString("country", "US");
        Locale locale = new Locale(language, country);

        // Default
        String header = Application.getInstance().getResources().getString(R.string.home_header1) + Application.getInstance().getResources().getString(R.string.home_body1);
        String body = "";

        if (participant.getState().currentTestCycle == 0 && participant.getCurrentTestSession().getId() == 0) {
            // default
        }
        else if (participant.shouldCurrentlyBeInTestSession()) {
            // default
        }
        else if (participant.getState().currentTestCycle != 5) {
            TestCycle cycle = participant.getCurrentTestCycle();

            DateTime today = new DateTime().withTimeAtStartOfDay();
            DateTime schedStartDateMinusOne = cycle.getActualStartDate().minusDays(1);

            // After the cycle, one day before the start of the next session
            if (schedStartDateMinusOne.isEqual(today)) {
                header = Application.getInstance().getResources().getString(R.string.home_header5);

                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);
                DateTime endDate = participant.getCurrentTestCycle().getActualEndDate();
                String endDateFmt = fmt.print(endDate.minusDays(1));

                header = header.replace("{DATE}", endDateFmt);
                body = Application.getInstance().getResources().getString(R.string.home_body5);
            }
            // After the cycle before the start of the next session
            else if (cycle.getNumberOfTestsLeft() == cycle.getNumberOfTests() && participant.getState().currentTestCycle != 0) {
                header = Application.getInstance().getResources().getString(R.string.home_header4);

                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM d").withLocale(locale);
                DateTime startDate = participant.getCurrentTestCycle().getActualStartDate();
                String startDateFmt = fmt.print(startDate);

                DateTime endDate = cycle.getActualEndDate();
                String endDateFmt = fmt.print(endDate.minusDays(1));

                body = Application.getInstance().getResources().getString(R.string.home_body4).replace("{DATE1}", startDateFmt);
                body = body.replace("{DATE2}", endDateFmt);
            }
            // After 4th test of the day
            else if (participant.getCurrentTestDay().getNumberOfTestsLeft() == 0) {
                header = Application.getInstance().getResources().getString(R.string.home_header3);
                body = Application.getInstance().getResources().getString(R.string.home_body3);
            }
            // Open the app, no test, still in a cycle
            else if (cycle.getNumberOfTestsLeft() > 0) {
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

    private void showPostBaselineHints() {
        Hints.markShown(HINT_POST_BASELINE);

        final HintPointer niceJobHint = new HintPointer(getActivity(), landing_layout, false, false);
        niceJobHint.setText(ViewUtil.getString(R.string.popup_nicejob));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                niceJobHint.dismiss();
                getMainActivity().showHomeHint(getActivity());
            }
        };

        niceJobHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        niceJobHint.show();
    }
}
