package com.healthymedium.translation;

import com.healthymedium.translation.common.CopyDoc;

public class UpdateHASD {

    public static void main(String[] args) {

        System.out.println("");
        System.out.println("HealthyMedium Translation Tool ---------");
        System.out.println("Updating HASD --------------------------\n");

        CopyDoc copyDoc = new CopyDoc();
        if(!copyDoc.init()) {
            return;
        }
        copyDoc.updateHASD();

        System.out.println("\nthat's all folks");
    }

}
