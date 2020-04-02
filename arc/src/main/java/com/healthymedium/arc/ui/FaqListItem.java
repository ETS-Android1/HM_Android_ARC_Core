package com.healthymedium.arc.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.paths.informative.FAQAnswerScreen;
import com.healthymedium.arc.utilities.ViewUtil;

public class FaqListItem extends LinearLayout {

    String question = "";
    String answer = "";

    View borderTop;
    View borderBottom;
    TextView textView;
    OnClickListener listener = null;

    public FaqListItem(Context context, @StringRes int question, @StringRes int answer) {
        super(context);
        this.question = ViewUtil.getString(question);
        this.answer = ViewUtil.getString(answer);
        init(context, null);
    }

    public FaqListItem(Context context, @StringRes int label, OnClickListener listener) {
        super(context);
        this.question = ViewUtil.getString(label);
        this.listener = listener;
        init(context, null);
    }

    public FaqListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FaqListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context,R.layout.custom_faq_listitem,this);

        textView = view.findViewById(R.id.textviewQuestion);
        borderTop = view.findViewById(R.id.borderTop);
        borderBottom = view.findViewById(R.id.borderBottom);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FaqListItem);

        try {
            boolean borderTopEnabled = a.getBoolean(R.styleable.FaqListItem_enableBorderTop,false);
            boolean borderBottomEnabled = a.getBoolean(R.styleable.FaqListItem_enableBorderBottom,false);
            setBorderEnabled(borderTopEnabled,borderBottomEnabled);

            if(a.hasValue(R.styleable.FaqListItem_question)){
                question = a.getString(R.styleable.FaqListItem_question);
            }
            if(a.hasValue(R.styleable.FaqListItem_answer)){
                answer = a.getString(R.styleable.FaqListItem_answer);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

        if(question!=null){
            textView.setText(question);
        }

        if(listener==null) {
            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseFragment fragment = new FAQAnswerScreen(question, answer);
                    NavigationManager.getInstance().open(fragment);
                }
            });
        } else {
            setOnClickListener(listener);
        }
    }

    void setBorderEnabled(boolean topEnabled, boolean bottomEnabled){
        borderTop.setVisibility(topEnabled ? VISIBLE:GONE);
        borderBottom.setVisibility(bottomEnabled ? VISIBLE:GONE);
    }

}
