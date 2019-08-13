package com.healthymedium.arc.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EarningOverview {

    public String total_earnings;
    public Integer cycle;
    public String cycle_earnings;
    public Goals goals;

    public static EarningOverview getTestObject() {
        EarningOverview overview = new EarningOverview();
        overview.cycle = 1;
        overview.cycle_earnings = "0.50";
        overview.total_earnings = "$13.50";
        overview.goals.twentyOneSessions.name = "21-sessions";
        overview.goals.twentyOneSessions.completed = true;
        overview.goals.twentyOneSessions.amount_earned = "$5.00";
        overview.goals.twentyOneSessions.progress = 21;
        overview.goals.twentyOneSessions.value = "$5.00";
        overview.goals.twoADay.name = "2-a-day";
        overview.goals.twoADay.completed = false;
        overview.goals.twoADay.amount_earned = "$0.00";
        overview.goals.twoADay.progress = 1;
        overview.goals.twoADay.value = "$6.00";
        overview.goals.fourOutOfFour.name = "4-out-of-4";
        overview.goals.fourOutOfFour.completed = false;
        overview.goals.fourOutOfFour.amount_earned = "$0.00";
        overview.goals.fourOutOfFour.progress = 1;
        overview.goals.fourOutOfFour.value = "$1.00";
        return overview;
    }

    public EarningOverview() {
        total_earnings = new String();
        cycle = new Integer(0);
        cycle_earnings = new String();
        goals = new Goals();
    }

    public class Goals {

        @SerializedName("21-sessions")
        public Goal twentyOneSessions;
        @SerializedName("2-a-day")
        public Goal twoADay;
        @SerializedName("4-out-of-4")
        public Goal fourOutOfFour;

        public Goals() {
            twentyOneSessions = new Goal();
            twoADay = new Goal();
            fourOutOfFour = new Goal();
        }

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

            Goal(){
                name = new String();
                value = new String();
                progress = new Integer(0);
                amount_earned = new String();
                completed = new Boolean(false);
            }
        }

    }

}
