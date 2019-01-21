package com.healthymedium.arc.api.tests.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SymbolTestSection {

    @SerializedName("appearance_time")
    public Double appearanceTime;
    @SerializedName("selection_time")
    public Double selectionTime;
    public Integer selected;
    public Integer correct;
    public List<List<String>> options;
    public List<List<String>> choices;

}
