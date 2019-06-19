package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
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
import com.healthymedium.arc.study.Study;

@SuppressLint("ValidFragment")
public class TestInfoTemplate extends BaseFragment {

    String stringTestNumber;
    String stringHeader;
    String stringBody;
    String stringButton;

    Drawable buttonImage;

    TextView textViewTestNumber;
    TextView textViewHeader;
    TextView textViewBody;

    LinearLayout content;

    Button button;

    public TestInfoTemplate(String testNumber, String header, String body, @Nullable String buttonText) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        stringButton = buttonText;
    }

    public TestInfoTemplate(String testNumber, String header, String body, @Nullable Drawable buttonImage) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        this.buttonImage = buttonImage;
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
