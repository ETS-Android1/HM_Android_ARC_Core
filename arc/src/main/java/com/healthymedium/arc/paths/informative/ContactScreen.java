package com.healthymedium.arc.paths.informative;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

import static com.healthymedium.arc.study.Study.TAG_CONTACT_INFO;

public class ContactScreen extends BaseFragment {

    String stringHeader;
    String stringPhoneNumber;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewPhoneNumber;

    TextView textViewAbout;
    TextView textViewPrivacyPolicy;
    Button button;

    public ContactScreen() {
        stringHeader = "I would like to <b>contact study site</b>.";
        stringPhoneNumber = PreferencesManager.getInstance().getString(TAG_CONTACT_INFO ,"555-555-5555");
        allowBackPress(false);
        setEnterTransitionRes(R.anim.slide_in_right,R.anim.slide_in_left);
        setExitTransitionRes(R.anim.slide_out_left,R.anim.slide_out_right);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewPhoneNumber = view.findViewById(R.id.textViewSubHeader);
        textViewPhoneNumber.setText(stringPhoneNumber);

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
                String number = stringPhoneNumber.replace("-","");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });

        textViewBack.setVisibility(View.VISIBLE);



        textViewAbout = view.findViewById(R.id.textViewAbout);
        ViewUtil.underlineTextView(textViewAbout);
        textViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutScreen aboutScreen = new AboutScreen();
                NavigationManager.getInstance().open(aboutScreen);
            }
        });

        textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        ViewUtil.underlineTextView(textViewPrivacyPolicy);
        textViewPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getPrivacyPolicy().show(getContext());
            }
        });

        return view;
    }

}
