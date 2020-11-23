package com.healthymedium.arc.api.tests.data;

import com.google.gson.annotations.SerializedName;

public class GridTestTap {

    public int x;
    public int y;

    // only used in grid v1
    @SerializedName("selection_time")
    public Double selectionTime;

    // only used in grid v2
    @SerializedName("grid_selection_time")
    public Double gridSelectionTime;
    @SerializedName("image_selection_time")
    public Double imageSelectionTime;
    public String image;

}
