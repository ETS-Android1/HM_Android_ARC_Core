package com.healthymedium.translations;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class TranslationTool {

    public static void main(String[] args) {

        System.out.println("\nHealthyMedium Translation Tool\n------------------------------\n");

        // check that paths are correctly set up for file creation
        if(!LocaleResource.localPathsSet()) {
            System.out.println("ERROR: Please define local paths in LocaleResource.java\n\nExiting");
            return;
        }

        System.out.println("grabbing translation data from the google sheet");
        List<LocaleResource>  data = null;
        try {
            data = TranslationDoc.grabData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        if (data == null) {
            return;
        }
        System.out.println("translation data received");



        System.out.println("sanitizing translation data");
        data = TranslationDoc.sanitizeData(data);



        System.out.println("exporting xml files for android to consume");
        TranslationDoc.createXMLfiles(data);



        System.out.println("\nthat's all folks");
    }

}
