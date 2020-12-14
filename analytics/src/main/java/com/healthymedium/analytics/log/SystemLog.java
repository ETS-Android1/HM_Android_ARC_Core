package com.healthymedium.analytics.log;

import android.content.Context;

public class SystemLog extends BaseLog {

    public SystemLog(Context context) {
        super(context,"system.log",true);
    }

    public SystemLog() {
        super();
    }

}
