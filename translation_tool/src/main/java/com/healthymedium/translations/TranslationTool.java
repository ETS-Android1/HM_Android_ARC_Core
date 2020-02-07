package com.healthymedium.translations;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class TranslationTool {

    public static void main(String[] args) {

        System.out.println("\nHealthyMedium Translation Tool\n------------------------------\n");

        System.out.println("trying to grab data from the google doc");
        List<List<String>>  data = null;
        try {
            data = TranslationDoc.grabData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        if (data == null || data.isEmpty()) {
            System.out.println("No data available, quiting");
            return;
        }
        System.out.println("hey, we've got something");

        System.out.println("parsing data into structures we can deal with");
        List<LocaleResource> resources = new ArrayList<>();
        List<String> localeNames = data.get(0);

        for(int i=1;i<localeNames.size();i++) {
            LocaleResource resource = new LocaleResource();
            resource.name = localeNames.get(i);
            resources.add(resource);
        }

        for(int i=1;i<data.size();i++) {
            List<String> row = data.get(i);
            String key = row.get(0);
            for(int j=1;j<row.size();j++) {
                String value = row.get(j);
                resources.get(j-1).map.put(key,value);
            }
        }

        System.out.println("sanitizing translation data");

        // todo: filter data

        System.out.println("exporting xml files for android to consume");

        // todo: format into xml files that android can use

        System.out.println("that's all folks");

    }

}
