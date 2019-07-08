package com.healthymedium.arc.custom;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;

public class DialogButtonTutorial extends LinearLayout {

    public TextView header;
    public TextView body;
    public TextView button;

    public DialogButtonTutorial(Context context) {
        super(context);
        init(context);
    }

    public DialogButtonTutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DialogButtonTutorial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context, R.layout.dialog_btn_tutorial,this);
        header = view.findViewById(R.id.dialogBtnTutorialHeader);
        body = view.findViewById(R.id.dialogBtnTutorialBody);
        button = view.findViewById(R.id.button);

        SpannableString content = new SpannableString("View a Tutorial");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        button.setText(content);
    }

}
