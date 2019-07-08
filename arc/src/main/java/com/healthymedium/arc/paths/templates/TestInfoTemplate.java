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
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.AboutScreen;
import com.healthymedium.arc.paths.tutorials.GridTutorial;
import com.healthymedium.arc.paths.tutorials.PricesTutorial;
import com.healthymedium.arc.paths.tutorials.SymbolTutorial;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.NavigationManager;

@SuppressLint("ValidFragment")
public class TestInfoTemplate extends BaseFragment {

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

        textViewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stringType.equals("grids")) {
                    GridTutorial gridTutorial = new GridTutorial();
                    NavigationManager.getInstance().open(gridTutorial);
                }
                else if (stringType.equals("symbols")) {
                    SymbolTutorial symbolTutorial = new SymbolTutorial();
                    NavigationManager.getInstance().open(symbolTutorial);
                }
                else if (stringType.equals("prices")) {
                    PricesTutorial pricesTutorial = new PricesTutorial();
                    NavigationManager.getInstance().open(pricesTutorial);
                }
            }
        });

//        if (stringSubHeader == "") {
//            textViewSubheader.setVisibility(View.GONE);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//            float dpRatio = getResources().getDisplayMetrics().density;
//            int side = (int)(32 * dpRatio);
//            int top = (int)(15 * dpRatio);
//
//            params.setMargins(side,top,side,0);
//            textViewBody.setLayoutParams(params);
//        }

        button = view.findViewById(R.id.button);
        if(stringButton!=null){
            button.setText(stringButton);
        } else if (buttonImage!=null) {
            button.setIcon(buttonImage);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openNextFragment();
            }
        });

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

}
