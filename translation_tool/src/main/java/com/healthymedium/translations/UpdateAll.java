package com.healthymedium.translations;

import com.healthymedium.translations.common.CopyDoc;

public class UpdateAll {

    public static void main(String[] args) {

        System.out.println("");
        System.out.println("HealthyMedium Translation Tool ---------");
        System.out.println("Updating All ---------------------------\n");

        CopyDoc copyDoc = new CopyDoc();
        if(!copyDoc.init()) {
            return;
        }
        copyDoc.updateCore();
        copyDoc.updateHASD();
        copyDoc.updateEXR();

        System.out.println("\nthat's all folks");
    }

}
