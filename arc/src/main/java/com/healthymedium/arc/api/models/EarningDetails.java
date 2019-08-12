package com.healthymedium.arc.api.models;

import java.util.List;

public class EarningDetails {

    public String total_earnings;
    public List<Cycle> cycles;

    public class Cycle {
        public Integer cycle;
        public String total;
        public Long start_date;
        public Long end_date;
        public List<Goal> details;
    }

    public class Goal {
        public String name;
        public String value;
        public Integer count_completed;
        public String amount_earned;
    }

}
