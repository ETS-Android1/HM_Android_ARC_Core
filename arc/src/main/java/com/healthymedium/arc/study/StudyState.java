package com.healthymedium.arc.study;


import com.healthymedium.arc.api.tests.data.BaseData;

import java.util.ArrayList;
import java.util.List;

public class StudyState {

    public int lifecycle;
    public int currentPath;
    public List<BaseData> cache = new ArrayList<>();
    public List<PathSegment> segments = new ArrayList<>();

}
