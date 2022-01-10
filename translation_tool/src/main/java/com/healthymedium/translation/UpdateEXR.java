package com.healthymedium.translation;

import com.healthymedium.translation.common.CopyDoc;

public class UpdateEXR {

    public static void main(String[] args) {

        System.out.println("");
        System.out.println("HealthyMedium Translation Tool ---------");
        System.out.println("Updating EXR ---------------------------\n");

        CopyDoc copyDoc = new CopyDoc();
        if(!copyDoc.init()) {
            return;
        }
        copyDoc.updateEXR();

        System.out.println("\nthat's all folks");
    }

}
