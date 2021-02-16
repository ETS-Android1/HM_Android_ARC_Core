package com.healthymedium.arc.api.tests.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GridTestSection {

    @SerializedName("display_symbols")
    public Double displaySymbols;
    @SerializedName("display_distraction")
    public Double displayDistraction;
    @SerializedName("display_test_grid")
    public Double displayTestGrid;
    @SerializedName("e_count")
    public int eCount;
    @SerializedName("f_count")
    public int fCount;
    public List<GridTestImage> images;
    public List<GridTestTap> choices;

}
