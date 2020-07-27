package com.healthymedium.translations;

import com.healthymedium.translations.common.CopyDoc;
import com.healthymedium.translations.common.LocaleResource;
import com.healthymedium.translations.common.GoogleDoc;

public class UpdateCore {

    public static void main(String[] args) {

        System.out.println("");
        System.out.println("HealthyMedium Translation Tool ---------");
        System.out.println("Updating Core --------------------------\n");

        CopyDoc copyDoc = new CopyDoc();
        if(!copyDoc.init()) {
            return;
        }
        copyDoc.updateCore();

        System.out.println("\nthat's all folks");
    }

}
