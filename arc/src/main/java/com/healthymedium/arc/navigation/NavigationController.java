package com.healthymedium.arc.navigation;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;

import com.healthymedium.analytics.Analytics;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.utilities.Log;

public class NavigationController {

    protected static String tag = "NavigationController";

    protected FragmentManager fragmentManager;
    protected Listener listener;

    protected int currentFragmentId = -1;
    protected int containerViewId = -1;

    public NavigationController(FragmentManager fragmentManager, @IdRes int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public void removeListener() {
        listener = null;
    }

    public void open(BaseFragment fragment) {
        if (fragmentManager != null) {
            TransitionSet transitions = fragment.getTransitionSet();
            String tag = fragment.getSimpleTag();
            Log.behavior.i("navigation","open '"+tag+"'");
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            transitions.enter,
                            transitions.exit,
                            transitions.popEnter,
                            transitions.popExit)
                    .replace(containerViewId, fragment,tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
            if(listener!=null){
                listener.onOpen();
            }
        }
    }

    public void open(BaseFragment fragment, TransitionSet transitions) {
        if (fragmentManager != null) {
            String tag = fragment.getSimpleTag();
            Log.behavior.i("navigation","open '"+tag+"'");
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            transitions.enter,
                            transitions.exit,
                            transitions.popEnter,
                            transitions.popExit)
                    .replace(containerViewId, fragment,tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
            if(listener!=null){
                listener.onOpen();
            }
        }
    }

    public void popBackStack() {
        if (fragmentManager != null) {
            Log.behavior.i("navigation","pop backstack");
            fragmentManager.popBackStack();
            if(listener!=null){
                listener.onPopBack();
            }
        }
    }

    public int getBackStackEntryCount() {
        return fragmentManager.getBackStackEntryCount();
    }

    public void clearBackStack() {
        try {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < count; i++) {
                int id = fragmentManager.getBackStackEntryAt(i).getId();
                fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } catch (IllegalStateException e) {
            Analytics.logException(Analytics.WARNING,tag,e);
        }
    }

    public BaseFragment getCurrentFragment(){
        return (BaseFragment) fragmentManager.findFragmentById(currentFragmentId);
    }

    public interface Listener {
        void onOpen();
        void onPopBack();
    }

}
