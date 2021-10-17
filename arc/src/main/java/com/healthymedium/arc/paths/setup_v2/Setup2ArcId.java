package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2ArcId extends Setup2Template {

    public Setup2ArcId() {
        super(6, 0, ViewUtil.getString(R.string.login_enter_ARCID));
    }

    @Override
    protected boolean shouldAutoProceed() {
        return true;
    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();

        if(Config.REST_BLACKHOLE) {
            Study.getInstance().openNextFragment();
            return;
        }

        SetupPathData setupPathData = ((SetupPathData)Study.getCurrentSegmentData());
        setupPathData.id = characterSequence.toString();
        Study.getInstance().openNextFragment();
    }

    private boolean fragmentExists(PathSegment path, Class tClass) {
        int last = path.fragments.size()-1;
        String oldName = path.fragments.get(last).getSimpleTag();
        String newName = tClass.getSimpleName();
        return oldName.equals(newName);
    }
}
