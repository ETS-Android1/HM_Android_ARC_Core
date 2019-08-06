package com.healthymedium.arc.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.MainActivity;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
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

    boolean enabled = true;

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

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(!enabled){
            return true;
        }
        return super.dispatchTouchEvent(ev);
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

    public void showHomeHint(final Activity activity) {

        enabled = false;

        final HintPointer homeHint = new HintPointer(activity, home, true, true);
        homeHint.setText(ViewUtil.getString(R.string.popup_tab_home));

        final HintHighlighter homeHighlight = new HintHighlighter(activity);
        homeHighlight.addTarget(home, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeHint.dismiss();
                homeHighlight.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        showProgressHint(activity);
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        homeHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        homeHint.show();
        homeHighlight.show();
    }

    public void showProgressHint(final Activity activity) {
        final HintPointer progressHint = new HintPointer(activity, progress, true, true);
        progressHint.setText(ViewUtil.getString(R.string.popup_tab_progress));

        final HintHighlighter progressHighlight = new HintHighlighter(activity);
        progressHighlight.addTarget(progress, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressHint.dismiss();
                progressHighlight.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        showEarningsHint(activity);
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        progressHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        progressHint.show();
        progressHighlight.show();
    }

    public void showEarningsHint(final Activity activity) {
        final HintPointer earningsHint = new HintPointer(activity, earnings, true, true);
        earningsHint.setText(ViewUtil.getString(R.string.popup_tab_earnings));

        final HintHighlighter earningsHighlight = new HintHighlighter(activity);
        earningsHighlight.addTarget(earnings, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                earningsHint.dismiss();
                earningsHighlight.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        showResourcesHint(activity);
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        earningsHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        earningsHint.show();
        earningsHighlight.show();
    }

    public void showResourcesHint(Activity activity) {
        final HintPointer resourcesHint = new HintPointer(activity, resources, true, true);
        resourcesHint.setText(ViewUtil.getString(R.string.popup_tab_resources));

        final HintHighlighter resourcesHighlight = new HintHighlighter(activity);
        resourcesHighlight.addTarget(resources, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resourcesHint.dismiss();
                resourcesHighlight.dismiss();

                enabled = true;
            }
        };

        resourcesHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        resourcesHint.show();
        resourcesHighlight.show();
    }

}
