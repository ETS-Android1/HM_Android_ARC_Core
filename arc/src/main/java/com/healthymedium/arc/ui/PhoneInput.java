package com.healthymedium.arc.ui;

import android.content.Context;
import android.graphics.Rect;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PhoneInput extends androidx.appcompat.widget.AppCompatEditText {

    Listener listener;

    public PhoneInput(Context context) {
        super(context);
        init(context);
    }

    public PhoneInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhoneInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int dp10 = ViewUtil.dpToPx(10);
        int dp16 = ViewUtil.dpToPx(16);
        setPadding(dp16,dp10,dp16,dp10);
        setBackgroundResource(R.drawable.edit_text);
        setInputType(InputType.TYPE_CLASS_PHONE);
        setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        setMinimumWidth(ViewUtil.dpToPx(240));
        setTextColor(ContextCompat.getColor(context,R.color.primary));
    }

    public void setMaxLength(int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        setFilters(fArray);
    }

    public String getString() {
        return getText().toString();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onValueChanged();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && isEnabled() && isFocusable()) {
            setTypeface(Fonts.robotoBold);

            post(new Runnable() {
                @Override
                public void run() {
                    final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(getRootView(),InputMethodManager.SHOW_IMPLICIT);
                }
            });
        } else if(!gainFocus){
            setTypeface(Fonts.roboto);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(listener!=null){
            listener.onValueChanged();
        }
    }
}
