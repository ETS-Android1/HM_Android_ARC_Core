package com.healthymedium.arc.api.models;

import java.util.List;

public class EarningDetails {

    String total_earnings;
    List<Cycle> cycles;

    public class Cycle {
        Integer cycle;
        String total;
        Long start_date;
        Long end_date;
        List<Goal> details;
    }

    public class Goal {
        String name;
        String value;
        Integer count_completed;
        String amount_earned;
    }

}
