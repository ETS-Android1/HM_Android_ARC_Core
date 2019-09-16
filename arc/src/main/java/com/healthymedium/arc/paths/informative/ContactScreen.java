package com.healthymedium.arc.paths.informative;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
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

    TextView textViewEmailHeader;
    TextView textViewEmailAddress;

//    TextView textViewAbout;
//    TextView textViewPrivacyPolicy;
    Button button;
    Button emailButton;

    public ContactScreen() {
        stringHeader = "I would like to <b>contact study site</b>.";
        stringPhoneNumber = PreferencesManager.getInstance().getString(TAG_CONTACT_INFO ,"555-555-5555");
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewPhoneNumber = view.findViewById(R.id.textViewSubHeader);
        textViewPhoneNumber.setText(stringPhoneNumber);

        textViewEmailHeader = view.findViewById(R.id.textViewEmailHeader);
        textViewEmailHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.contact_email1)));

        textViewEmailAddress = view.findViewById(R.id.textViewEmailSubHeader);
        textViewEmailAddress.setText("sample@email.com");

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

        emailButton = view.findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String number = stringPhoneNumber.replace("-","");
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
//                startActivity(intent);
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        return view;
    }

}
