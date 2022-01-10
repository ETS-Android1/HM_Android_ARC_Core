package com.healthymedium.translation;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiffTranslations {
    public static void main(String[] args) throws IOException {
        exectue();
    }

    private static void exectue() throws IOException {

        Gson gson = new Gson();
        // This file should be store at the root of the Git repo for the app.
        // NOT the root repo of ARCCore.
        File oldFile = new File("translations/exr_translation1.14.json");
        BufferedReader oldBr = new BufferedReader(new FileReader(oldFile));
        TranslateInvArc.iOSLanguageMap oldMap = gson.fromJson(
                oldBr, TranslateInvArc.iOSLanguageMap.class);

        File newFile = new File("translations/exr_translation2.0.json");
        BufferedReader newBr = new BufferedReader(new FileReader(newFile));
        TranslateInvArc.iOSLanguageMap newMap = gson.fromJson(
                newBr, TranslateInvArc.iOSLanguageMap.class);

        LinkedTreeMap<String, LanguageFileChange> diffs = new LinkedTreeMap<>();
        LinkedTreeMap<Integer, List<LanguageFileChange>> changeMap = new LinkedTreeMap<>();
        LinkedTreeMap<Integer, List<LanguageFileChange>> removedMap = new LinkedTreeMap<>();
        LinkedTreeMap<Integer, List<LanguageFileChange>> addedMap = new LinkedTreeMap<>();

        for(int i = 0; i < oldMap.versions.size(); i++) {
            addDiffsForMaps(oldMap.versions.get(i).map, newMap.versions.get(i).map, diffs);
            addDiffsForMaps(newMap.versions.get(i).map, oldMap.versions.get(i).map, diffs);

            List<LanguageFileChange> removed = new ArrayList<>();
            List<LanguageFileChange> added = new ArrayList<>();
            List<LanguageFileChange> changed = new ArrayList<>();

            StringBuilder changeLog = new StringBuilder("\n\n");

            for (String key: diffs.keySet()) {
                LanguageFileChange change = diffs.get(key);
                if (change.wasRemoved) {
                    removed.add(change);
                } else if (change.wasAdded) {
                    added.add(change);
                } else if (!change.oldValue.equals(change.newValue)) {
                    changed.add(change);
                    changeLog.append("----Key----\n");
                    changeLog.append(key);
                    changeLog.append("\n");
                    changeLog.append("----From----\n");
                    changeLog.append(change.oldValue);
                    changeLog.append("\n----To----\n");
                    changeLog.append(change.newValue);
                    changeLog.append("\n\n");
                }
            }

            changeMap.put(i, changed);
            removedMap.put(i, removed);
            addedMap.put(i, added);

            // Only print out English changes, the rest should be the same
            if (i == 0) {
                System.out.println(changeLog.toString());
            }
        }
    }

    public static void addDiffsForMaps(LinkedTreeMap<String, String> oldMap,
                                       LinkedTreeMap<String, String> newMap,
                                       LinkedTreeMap<String, LanguageFileChange> diffs) {

        for (String key : oldMap.keySet()) {
            if (!diffs.containsKey(key)) {
                String oldValue = oldMap.get(key);
                String newValue = findNewValue(newMap, key);
                if (newValue == null) {
                    diffs.put(key, new LanguageFileChange(key,
                            oldValue, null, false, true));
                } else {
                    diffs.put(key, new LanguageFileChange(key,
                            oldValue, newValue, false, false));
                }
            }
        }
    }

    public static String findNewValue(LinkedTreeMap<String, String> newMap, String keyToFind) {
        for (String key : newMap.keySet()) {
            if (key.equals(keyToFind)) {
                return newMap.get(key);
            }
        }
        return null;
    }

    public static class LanguageFileChange {
        public String key;
        public String oldValue;
        public String newValue;
        public boolean wasAdded;
        public boolean wasRemoved;

        public LanguageFileChange(
                String key,
                String oldValue,
                String newValue,
                boolean wasAdded,
                boolean wasRemoved
        ) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.wasAdded = wasAdded;
            this.wasRemoved = wasRemoved;
        }
    }
}
