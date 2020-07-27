package com.healthymedium.translations;

import com.healthymedium.translations.common.CopyDoc;
import com.healthymedium.translations.common.LocaleResource;
import com.healthymedium.translations.common.GoogleDoc;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

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
