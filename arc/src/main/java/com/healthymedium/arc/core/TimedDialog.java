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
public class TimedDialog extends DialogFragment {

    String text;
    long duration;

    TextView textView;
    OnDialogDismiss listener;
    Handler handler;

    @SuppressLint("ValidFragment")
    public TimedDialog(String text, long duration){
        this.text = text;
        this.duration = duration;
    }

    public TimedDialog(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_TITLE, R.style.AppTheme);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_timed, container, false);
        textView = v.findViewById(R.id.textviewTimedDialog);
        textView.setText(text);

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
