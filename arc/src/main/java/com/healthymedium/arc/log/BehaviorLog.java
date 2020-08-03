package com.healthymedium.arc.log;

public class BehaviorLog extends BaseLog {

    public BehaviorLog() {
        super("behavior.log",false);
        write("--------------------\n");
    }

}
