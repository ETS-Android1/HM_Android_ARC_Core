package com.healthymedium.arc.navigation;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;

import java.util.ArrayList;
import java.util.List;

public class SlidingNavigationController extends NavigationController {

    List<BaseFragment> fragments = new ArrayList<>();
    int currentIndex;

    public SlidingNavigationController(FragmentManager fragmentManager, @IdRes int containerViewId) {
        super(fragmentManager,containerViewId);
    }

    public void setFragmentSet(List<BaseFragment> fragments) {
        this.fragments = fragments;
    }

    public void addFragmentToSet(BaseFragment fragment) {
        this.fragments.add(fragment);
    }

    @Override
    public void open(BaseFragment fragment) {
        if(!fragments.contains(fragment)) {
            fragments.add(fragment);
        }
        int index = fragments.indexOf(fragment);
        if(index<currentIndex) {
            openLeft(fragment);
        } else {
            openRight(fragment);
        }
        currentIndex = index;
    }

    private void openLeft(BaseFragment fragment) {
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_left;
        set.popEnter =  R.anim.slide_in_right;
        set.exit = R.anim.slide_out_right;
        set.popExit =  R.anim.slide_out_left;
        open(fragment,set);
    }

    private void openRight(BaseFragment fragment) {
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_right;
        set.popEnter =  R.anim.slide_in_left;
        set.exit = R.anim.slide_out_left;
        set.popExit =  R.anim.slide_out_right;
        open(fragment,set);
    }



}
