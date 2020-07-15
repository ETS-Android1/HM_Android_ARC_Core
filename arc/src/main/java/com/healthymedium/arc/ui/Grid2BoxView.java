package com.healthymedium.arc.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2BoxView extends LinearLayout {

    int defaultWidth = ViewUtil.mmToPx(10);
    int defaultHeight = ViewUtil.mmToPx(16);

    RoundedLinearLayout roundedLinearLayout;
    ImageView imageView;

    Drawable image;

    boolean selected = false;
    boolean selectable = true;
    long timestamp = 0;
    int index = -1;

    @ColorRes int colorNormal = R.color.gridNormal;
    @ColorRes int colorSelected = R.color.grid2Selected;
    int strokeWidthNormal = 1;
    int strokeWidthSelected = 3;
    @ColorRes int strokeColorNormal = R.color.grid2NormalBorder;
    @ColorRes int strokeColorSelected = R.color.grid2SelectedBorder;

    Listener listener;

    public Grid2BoxView(Context context) {
        super(context);
        init(context);
    }

    public Grid2BoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Grid2BoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_grid2_view,this);
        roundedLinearLayout = view.findViewById(R.id.linearLayout);
        imageView = view.findViewById(R.id.imageView);

        showSelectedState(false);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setImage(@DrawableRes int id){
        image = ViewUtil.getDrawable(id);
        showSelectedState(selected);
    }

    public void removeImage(){
        image = null;
        showSelectedState(selected);
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void setSelected(boolean selected) {
        if(this.selected==selected) {
            return;
        }
        this.selected = selected;
        timestamp = selected ? System.currentTimeMillis() : 0;
        showSelectedState(selected);
    }

    private void showSelectedState(boolean selected) {
        if(selected) {
            roundedLinearLayout.setFillColor(colorSelected);
            roundedLinearLayout.setStrokeWidth(strokeWidthSelected);
            roundedLinearLayout.setStrokeColor(strokeColorSelected);
            roundedLinearLayout.setElevation(ViewUtil.dpToPx(4));
        } else {
            roundedLinearLayout.setFillColor(colorNormal);
            roundedLinearLayout.setStrokeWidth(strokeWidthNormal);
            roundedLinearLayout.setStrokeColor(strokeColorNormal);
            roundedLinearLayout.setElevation(ViewUtil.dpToPx(0));
        }
        roundedLinearLayout.setRadius(6);

        if(image==null) {
            imageView.setImageDrawable(null);
        } else {
            imageView.setImageDrawable(image);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN && selectable) {
            setSelected(!selected);
            if(listener!=null) {
                return listener.onSelected(this, selected);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(defaultWidth, widthSize);
        } else {
            width = defaultWidth;
        }

        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(defaultHeight, heightSize);
        } else {
            height = defaultHeight;
        }

        setMeasuredDimension(width,height);

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++) {
            getChildAt(i).measure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    public interface Listener {
        boolean onSelected(Grid2BoxView view, boolean selected);
    }

}
