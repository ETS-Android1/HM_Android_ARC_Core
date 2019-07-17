package com.healthymedium.arc.custom;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.EarningsScreen;
import com.healthymedium.arc.paths.informative.ProgressScreen;
import com.healthymedium.arc.paths.informative.ResourcesScreen;
import com.healthymedium.arc.paths.templates.LandingTemplate;
import com.healthymedium.arc.utilities.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class BottomNavigationView extends LinearLayout {

    private MenuItem home;
    private MenuItem progress;
    private MenuItem earnings;
    private MenuItem resources;

    // ---------------------------------------------------------------------------------------------

    private MenuItem lastSelected;
    private int normalColor;
    private int selectedColor;

    public BottomNavigationView(Context context) {
        super(context);
        init(null,0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        Context context = getContext();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        normalColor = ViewUtil.getColor(getContext(),R.color.text);
        selectedColor = ViewUtil.getColor(getContext(),R.color.primary);

        home = new MenuItem(context,
                "Home",
                R.drawable.ic_home_inactive,
                R.drawable.ic_home_active,
                new OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().popBackStack();
                NavigationManager.getInstance().open(new LandingTemplate(false));
            }
        });

        progress = new MenuItem(context,
                "Progress",
                R.drawable.ic_progress_inactive,
                R.drawable.ic_progress_active,
                new OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().popBackStack();
                NavigationManager.getInstance().open(new ProgressScreen());
            }
        });

        earnings = new MenuItem(context,
                "Earnings",
                R.drawable.ic_earnings_inactive,
                R.drawable.ic_earnings_active,
                new OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().popBackStack();
                NavigationManager.getInstance().open(new EarningsScreen());
            }
        });

        resources = new MenuItem(context,
                "Resources",
                R.drawable.ic_resources_inactive,
                R.drawable.ic_resources_active,
                new OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().popBackStack();
                NavigationManager.getInstance().open(new ResourcesScreen());
            }
        });

        addView(home);
        addView(progress);
        addView(earnings);
        addView(resources);

        setPadding(0,0,0, ViewUtil.getNavBarHeight());

        // set home as default
        home.setSelected(true);
        lastSelected = home;
    }

    public class MenuItem extends LinearLayout {

        private Drawable drawableNormal;
        private Drawable drawableSelected;

        private ImageView imageView;
        private TextView textView;

        public MenuItem(Context context, String name, @DrawableRes int resNormal, @DrawableRes int resSelected, final OnClickListener listener){
            super(context);

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lastSelected!=null && lastSelected!=MenuItem.this){
                        lastSelected.setSelected(false);
                    }
                    setSelected(true);
                    lastSelected = MenuItem.this;
                    if(listener!=null){
                        listener.onClick(MenuItem.this);
                    }
                }
            });

            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(0,ViewUtil.dpToPx(8),0,ViewUtil.dpToPx(4));
            setLayoutParams(new LayoutParams(ViewUtil.dpToPx(80), ViewGroup.LayoutParams.MATCH_PARENT));

            imageView = new ImageView(context);
            addView(imageView);

            textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
            textView.setLineSpacing(ViewUtil.dpToPx(2),0);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            addView(textView);

            drawableNormal = ViewUtil.getDrawable(context,resNormal);
            drawableSelected = ViewUtil.getDrawable(context,resSelected);
            textView.setText(name);

            setSelected(false);
        }

        public void setSelected(boolean selected){
            imageView.setImageDrawable(selected ? drawableSelected : drawableNormal);
            textView.setTextColor(selected ? selectedColor : normalColor);
        }

    }

    public void openHome() {
        home.callOnClick();
    }

    public void openProgress() {
        progress.callOnClick();
    }

    public void openEarnings() {
        earnings.callOnClick();
    }

    public void openResources() {
        resources.callOnClick();
    }


}
