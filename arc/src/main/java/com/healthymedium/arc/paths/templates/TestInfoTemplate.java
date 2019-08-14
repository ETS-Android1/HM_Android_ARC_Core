package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.hints.Hints;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.tutorials.GridTutorial;
import com.healthymedium.arc.paths.tutorials.PricesTutorial;
import com.healthymedium.arc.paths.tutorials.SymbolTutorial;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class TestInfoTemplate extends BaseFragment {

    private static final String HINT_GRID_TUTORIAL = "HINT_GRID_TUTORIAL";
    private static final String HINT_PRICES_TUTORIAL = "HINT_PRICES_TUTORIAL";
    private static final String HINT_SYMBOL_TUTORIAL = "HINT_SYMBOL_TUTORIAL";
    private static final String HINT_REPEAT_TUTORIAL = "HINT_REPEAT_TUTORIAL";

    LinearLayout headerLayout;

    String stringTestNumber;
    String stringHeader;
    String stringBody;
    String stringButton;
    String stringType;

    Drawable buttonImage;

    TextView textViewTestNumber;
    TextView textViewHeader;
    TextView textViewBody;
    TextView textViewTutorial;

    LinearLayout content;

    Button button;

    HintPointer tutorialHint;
    HintHighlighter tutorialHintHighlighter;

    public TestInfoTemplate(String testNumber, String header, String body, String type, @Nullable String buttonText) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        stringButton = buttonText;
        stringType = type;
    }

    public TestInfoTemplate(String testNumber, String header, String body, String type, @Nullable Drawable buttonImage) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        this.buttonImage = buttonImage;
        stringType = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_test_info, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        headerLayout = view.findViewById(R.id.headerLayout);
        if (stringType.equals("grids")) {
            headerLayout.setBackground(ViewUtil.getDrawable(R.drawable.ic_grids_bg));
        }
        else if (stringType.equals("symbols")) {
            headerLayout.setBackground(ViewUtil.getDrawable(R.drawable.ic_symbols_bg));
        }
        else if (stringType.equals("prices")) {
            headerLayout.setBackground(ViewUtil.getDrawable(R.drawable.ic_prices_bg));
        }

        textViewTestNumber = view.findViewById(R.id.textViewTestNumber);
        textViewTestNumber.setText(stringTestNumber);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        //textViewHeader.setTypeface(Fonts.georgiaItalic);
        textViewHeader.setText(stringHeader);

        textViewBody = view.findViewById(R.id.textViewBody);
        textViewBody.setText(Html.fromHtml(stringBody));

        textViewTutorial = view.findViewById(R.id.textViewTutorial);
        // textViewTutorial.setText("View a Tutorial");
        SpannableString content = new SpannableString("View a Tutorial");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewTutorial.setText(content);
        textViewTutorial.setVisibility(View.VISIBLE);

        button = view.findViewById(R.id.button);
        if(stringButton!=null){
            button.setText(stringButton);
        } else if (buttonImage!=null) {
            button.setIcon(buttonImage);
        }

        // Show a hint if this test type's tutorial has not yet been completed
        if ((stringType.equals("grids") && !Hints.hasBeenShown(HINT_GRID_TUTORIAL))
                || (stringType.equals("prices") && !Hints.hasBeenShown(HINT_PRICES_TUTORIAL))
                || (stringType.equals("symbols") && !Hints.hasBeenShown(HINT_SYMBOL_TUTORIAL))) {
            tutorialHint = new HintPointer(getActivity(), textViewTutorial, true, false);
            tutorialHint.setText(ViewUtil.getString(R.string.popup_tutorial_view));
            tutorialHint.show();
        }
        else if (!Hints.hasBeenShown(HINT_REPEAT_TUTORIAL)) {
            Hints.markShown(HINT_REPEAT_TUTORIAL);

            tutorialHintHighlighter = new HintHighlighter(getActivity());
            tutorialHintHighlighter.addTarget(textViewTutorial, 5, 10);
            tutorialHintHighlighter.show();

            tutorialHint = new HintPointer(getActivity(), textViewTutorial, true, true);
            tutorialHint.setText(ViewUtil.getString(R.string.popup_tutorial_complete));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tutorialHint.dismiss();
                    tutorialHintHighlighter.dismiss();
                    enableButton();
                }
            };

            tutorialHint.addButton(ViewUtil.getString(R.string.popup_gotit), listener);
            tutorialHint.show();
        }
        // If the tutorial has been completed, enable the test button
        else {
            enableButton();
        }

        textViewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tutorialHint!=null) {
                    tutorialHint.dismiss();
                }

                if (tutorialHintHighlighter!=null) {
                    tutorialHintHighlighter.dismiss();
                }

                if (stringType.equals("grids")) {
                    Hints.markShown(HINT_GRID_TUTORIAL);
                    GridTutorial gridTutorial = new GridTutorial();
                    NavigationManager.getInstance().open(gridTutorial);
                }
                else if (stringType.equals("symbols")) {
                    Hints.markShown(HINT_SYMBOL_TUTORIAL);
                    SymbolTutorial symbolTutorial = new SymbolTutorial();
                    NavigationManager.getInstance().open(symbolTutorial);
                }
                else if (stringType.equals("prices")) {
                    Hints.markShown(HINT_PRICES_TUTORIAL);
                    PricesTutorial pricesTutorial = new PricesTutorial();
                    NavigationManager.getInstance().open(pricesTutorial);
                }
            }
        });

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    private void enableButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tutorialHint!=null) {
                    tutorialHint.dismiss();
                }

                if (tutorialHintHighlighter!=null) {
                    tutorialHintHighlighter.dismiss();
                }

                Study.getInstance().openNextFragment();
            }
        });
    }

}
