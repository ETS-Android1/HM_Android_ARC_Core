package com.healthymedium.arc.core;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.library.R;

@SuppressLint("ValidFragment")
public class TimedDialogMultipart extends DialogFragment {

    String text1;
    String text2;
    long textChangeDuration;
    long duration;

    TextView textView;
    OnDialogDismiss listener;
    Handler handler;
    Handler changeHandler;

    @SuppressLint("ValidFragment")
    public TimedDialogMultipart(String text1, String text2, long textChangeDuration, long duration){
        this.text1 = text1;
        this.text2 = text2;
        this.textChangeDuration = textChangeDuration;
        this.duration = duration;
    }

    public TimedDialogMultipart(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_TITLE, R.style.AppTheme);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_timed_multipart, container, false);
        textView = v.findViewById(R.id.textviewTimedDialog);
        textView.setText(text1);

        changeHandler = new Handler();
        changeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               textView.setText(text2);
            }
        }, textChangeDuration);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(listener != null) {
                    listener.dismiss();
                }
                if(handler!=null){
                    dismiss();
                }
            }
        }, duration);
        return v;
    }

    public void setOnDialogDismissListener(OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss();
    }
}
