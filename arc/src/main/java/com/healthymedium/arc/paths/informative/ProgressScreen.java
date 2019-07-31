package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class ProgressScreen extends BaseFragment {

    TextView weeklyStatus;
    TextView startDate;
    TextView startDate_date;
    TextView endDate;
    TextView endDate_date;
    TextView joinedDate;
    TextView joinedDate_date;
    TextView finishDate;
    TextView finishDate_date;
    TextView timeBetween;
    TextView timeBetween_units;

    public ProgressScreen() {
        allowBackPress(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        weeklyStatus = view.findViewById(R.id.weeklyStatus);
        weeklyStatus.setText(Html.fromHtml(ViewUtil.getString(R.string.progess_weeklystatus).replace("{#}", "x")));

        startDate = view.findViewById(R.id.startDate);
        startDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_startdate)));

        startDate_date = view.findViewById(R.id.startDate_date);
        startDate_date.setText(ViewUtil.getString(R.string.progress_startdate_date).replace("{DATE}", "x"));

        endDate = view.findViewById(R.id.endDate);
        endDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_enddate)));

        endDate_date = view.findViewById(R.id.endDate_date);
        endDate_date.setText(ViewUtil.getString(R.string.progress_enddate_date).replace("{DATE}", "x"));

        joinedDate = view.findViewById(R.id.joinedDate);
        joinedDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_joindate)));

        joinedDate_date = view.findViewById(R.id.joinedDate_date);
        joinedDate_date.setText(ViewUtil.getString(R.string.progress_joindate_date).replace("{DATE}", "x"));

        finishDate = view.findViewById(R.id.finishDate);
        finishDate.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_finishdate)));

        finishDate_date = view.findViewById(R.id.finishDate_date);
        finishDate_date.setText(ViewUtil.getString(R.string.progress_finishdate_date).replace("{DATE}", "x"));

        timeBetween = view.findViewById(R.id.timeBetween);
        timeBetween.setText(Html.fromHtml(ViewUtil.getString(R.string.progress_timebtwtesting)));

        timeBetween_units = view.findViewById(R.id.timeBetween_units);
        String units = ViewUtil.getString(R.string.progress_timebtwtesting_unit);
        units = units.replace("{#}", "x");
        units = units.replace("{UNIT}", "units");
        timeBetween_units.setText(units);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
    }
}
