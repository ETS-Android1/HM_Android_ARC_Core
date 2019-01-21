package com.healthymedium.arc.study;


import java.util.ArrayList;
import java.util.List;

public class StudyState {

    public int lifecycle;
    public int currentPath;
    public List<Object> cache = new ArrayList<>();
    public List<PathSegment> segments = new ArrayList<>();

}
