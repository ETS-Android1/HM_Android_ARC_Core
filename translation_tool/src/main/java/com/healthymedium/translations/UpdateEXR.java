package com.healthymedium.translations;

import com.healthymedium.translations.common.CopyDoc;
import com.healthymedium.translations.common.LocaleResource;
import com.healthymedium.translations.common.GoogleDoc;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class UpdateEXR {

    public static void main(String[] args) {

        System.out.println("");
        System.out.println("HealthyMedium Translation Tool ---------");
        System.out.println("Updating EXR ---------------------------\n");

        CopyDoc copyDoc = new CopyDoc();
        if(!copyDoc.init()) {
            return;
        }
        copyDoc.updateCore();
        copyDoc.updateEXR();

        System.out.println("\nthat's all folks");
    }

}
