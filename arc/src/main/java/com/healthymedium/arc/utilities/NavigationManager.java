package com.healthymedium.arc.utilities;

import android.os.SystemClock;

import androidx.fragment.app.FragmentManager;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

public class NavigationManager {

    private static NavigationManager instance;
    private FragmentManager fragmentManager;
    private NavigationListener navigationListener;

    private int currentFragmentId = -1;

    private NavigationManager() {
        // Make empty constructor private
    }

    public static synchronized void initializeInstance(final FragmentManager fragmentManager) {
        instance = new NavigationManager();
        instance.fragmentManager = fragmentManager;
    }

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NavigationManager.class.getSimpleName() + " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public NavigationListener getNavigationListener() {
        return navigationListener;
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    public void open(BaseFragment fragment) {
        if (fragmentManager != null) {
            int enterTransition = fragment.getEnterTransitionRes();
            int exitTransition = fragment.getExitTransitionRes();
            int popEnterTransition = fragment.getPopEnterTransitionRes();
            int popExitTransition = fragment.getPopExitTransitionRes();
            String tag = fragment.getClass().getName() + "." + SystemClock.uptimeMillis();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(enterTransition,exitTransition,popEnterTransition,popExitTransition)
                    .replace(R.id.content_frame, fragment, tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
            if(navigationListener!=null){
                navigationListener.onOpen();
            }
        }
    }

    public void popBackStack() {
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
            if(navigationListener!=null){
                navigationListener.onPopBack();
            }
        }
    }

    public int getBackStackEntryCount() {
        return fragmentManager.getBackStackEntryCount();
    }

    public void clearBackStack() {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            int id = fragmentManager.getBackStackEntryAt(i).getId();
            //String tag = fragmentManager.getBackStackEntryAt(i).getName();
            /*Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).disableFragmentAnimation = true;
            }*/
            fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public BaseFragment getCurrentFragment(){
        return (BaseFragment) fragmentManager.findFragmentById(instance.currentFragmentId);
    }

    public interface NavigationListener {
        void onOpen();
        void onPopBack();
    }


}
