package com.healthymedium.arc.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class SizeAwareTextView extends AppCompatTextView {

        private OnTextSizeChangedListener listener;
        private float textSize;

        public SizeAwareTextView(Context context) {
            super(context);
            textSize = getTextSize();
        }

        public SizeAwareTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            textSize = getTextSize();
        }

        public SizeAwareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            textSize = getTextSize();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (textSize != getTextSize()) {
                textSize = getTextSize();
                if (listener != null) {
                    listener.onTextSizeChanged(this, textSize);
                }
            }
        }

        public void setOnTextSizeChangedListener(OnTextSizeChangedListener onTextSizeChangedListener) {
            listener = onTextSizeChangedListener;
        }

        public interface OnTextSizeChangedListener {
            void onTextSizeChanged(SizeAwareTextView view, float px);
        }
    }