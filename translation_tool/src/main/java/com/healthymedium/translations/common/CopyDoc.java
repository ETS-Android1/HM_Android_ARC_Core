package com.healthymedium.translations.common;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CopyDoc {

    private static final String SPREADSHEET_ID = "16YqR3rxQMK5Xg8KnX4_H99gVxMeZEol_bHSbhxYakms";
    private static final String SETUP_FILE = "setup.json";

    GoogleDoc googleDoc;
    Setup setup;
    String path;

    public CopyDoc() {
    }

    public boolean init() {

        path = getPath();
        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdir();
        }

        // load setup json
        try {
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            InputStream inputStream = new FileInputStream(path + SETUP_FILE);
            if (inputStream == null) {
                System.out.println("setup file not found");
                Setup.createDefault(jsonFactory,path+SETUP_FILE);
                return false;
            }
            setup = Setup.load(jsonFactory, new InputStreamReader(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(setup==null){
            return false;
        }

        googleDoc = new GoogleDoc();
        googleDoc.setSpreadsheetId(SPREADSHEET_ID);
        return googleDoc.valid;
    }

    public boolean updateCore() {
        return update("Core!A2:Y999",setup.coreResourcePath);
    }

    public boolean updateEXR() {
        return update("EXR!A2:Y999",setup.exrResourcePath);
    }

    public boolean updateHASD() {
        return update("HASD!A2:Y999",setup.hasdResourcePath);
    }

    public boolean update(String range, String resourcePath) {
        if(!googleDoc.valid){
            return false;
        }

        List<List<String>> data = googleDoc.get(range);
        if(data.isEmpty()){
            return false;
        }

        List<LocaleResource> localeResources = new ArrayList<>();
        int size = data.get(0).size();
        for(int i=1; i<size; i++) {
            localeResources.add(parseForLocale(data,i));
        }

        if(localeResources.isEmpty()) {
            return false;
        }

        if(!resourcePath.endsWith(File.separator)) {
            resourcePath = resourcePath + File.separator;
        }

        for(LocaleResource resource : localeResources) {
            boolean written = resource.write(resourcePath);
            if(!written){
                return false;
            }
        }

        return true;
    }

    private LocaleResource parseForLocale(List<List<String>> data, int index) {
        Map<String,String> map = new LinkedHashMap<>();
        int size = data.size();
        for(List<String> row : data){
            if(row.isEmpty() || row.size() <= index) {
                continue;
            }

            String key = row.get(0);
            if(!key.isEmpty()){
                map.put(key,row.get(index));
            }
        }
        LocaleResource locale = new LocaleResource(map);
        return locale;
    }

    private String getPath() {
        String moduleName = "translation_tool";
        String sep = File.separator;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("").getFile());
        String firstHalf = file.getAbsolutePath().split(moduleName)[0];
        String path = firstHalf + moduleName + sep + "src" + sep + "main" + sep + "resources" + sep;
        return path;


    }

}
