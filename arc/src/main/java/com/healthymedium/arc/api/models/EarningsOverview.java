package com.healthymedium.arc.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EarningsOverview {

    String total_earnings;
    Integer cycle;
    String cycle_earnings;
    Goals goals;

    public class Goals {

        @SerializedName("21-sessions")
        public Goal twentyOneSessions;
        @SerializedName("2-a-day")
        public Goal twoADay;
        @SerializedName("4-out-of-4")
        public Goal fourOutOfFour;

        public class Goal {
            public String name;
            public String value;
            public Integer progress;
            public String amount_earned;
            public Boolean completed;
            public List<Integer> progress_components;
        }

    }

}
