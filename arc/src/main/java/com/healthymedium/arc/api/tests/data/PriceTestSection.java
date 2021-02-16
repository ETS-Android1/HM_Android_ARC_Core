package com.healthymedium.arc.api.tests.data;

import com.google.gson.annotations.SerializedName;

public class PriceTestSection {

    @SerializedName("good_price")
    public Integer goodPrice;
    @SerializedName("stimulus_display_time")
    public Double stimulusDisplayTime;
    @SerializedName("question_display_time")
    public Double questionDisplayTime;
    public String item;
    public String price;
    @SerializedName("alt_price")
    public String altPrice;
    @SerializedName("correct_index")
    public Integer correctIndex;
    @SerializedName("selected_index")
    public Integer selectedIndex;
    @SerializedName("selection_time")
    public Double selectionTime;

}
