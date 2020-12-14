package com.healthymedium.analytics.log;

import android.content.Context;

public class BehaviorLog extends BaseLog {

    public BehaviorLog(Context context) {
        super(context,"behavior.log",true);
        write("--------------------\n");
    }

    public BehaviorLog() {
        super();
    }

}
