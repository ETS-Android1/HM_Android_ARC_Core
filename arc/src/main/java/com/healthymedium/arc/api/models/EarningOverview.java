package com.healthymedium.arc.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EarningOverview {

    public String total_earnings;
    public Integer cycle;
    public String cycle_earnings;
    public Goals goals;

    public class Goals {

        @SerializedName("21-sessions")
        public Goal twentyOneSessions;
        @SerializedName("2-a-day")
        public Goal twoADay;
        @SerializedName("4-out-of-4")
        public Goal fourOutOfFour;

        public List<Goal> getList(){
            List list = new ArrayList<>();
            list.add(twentyOneSessions);
            list.add(twoADay);
            list.add(fourOutOfFour);
            return list;
        }

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
