package com.healthymedium.arc.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.healthymedium.arc.study.State;
import com.healthymedium.arc.study.StateCache;
import com.healthymedium.arc.study.StateMachine;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.analytics.Log;

import com.google.gson.JsonObject;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.TestSession;

public class MigrationUtil {

    public static final String TAG_VERSION_LIB = "versionLib";
    public static final String TAG_VERSION_APP = "versionApp";

    public void checkForUpdate(){

        // library migration

        long newLibVersion = VersionUtil.getLibraryVersionCode();
        long oldLibVersion = PreferencesManager.getInstance().getLong(TAG_VERSION_LIB, newLibVersion);

        Log.i("MigrationUtil", "old library version="+oldLibVersion);
        Log.i("MigrationUtil", "new library version="+newLibVersion);

        if(newLibVersion > oldLibVersion) {
            Log.i("MigrationUtil", "migrating library data from "+oldLibVersion+" to "+newLibVersion);
            if(migrateLibraryData(oldLibVersion,newLibVersion)) {
                PreferencesManager.getInstance().putLong(TAG_VERSION_LIB, newLibVersion);
            }
        }

        // app migration

        long newAppVersion = VersionUtil.getAppVersionCode();
        long oldAppVersion = PreferencesManager.getInstance().getLong(TAG_VERSION_APP, newAppVersion);

        Log.i("MigrationUtil", "old app version="+oldAppVersion);
        Log.i("MigrationUtil", "new app version="+newAppVersion);

        if(newAppVersion > newAppVersion) {
            Log.i("MigrationUtil", "migrating app data from "+oldAppVersion+" to "+newAppVersion);
            if(migrateAppData(oldAppVersion,newAppVersion)) {
                PreferencesManager.getInstance().putLong(TAG_VERSION_APP, newAppVersion);
                Log.i("MigrationUtil", "migration successful");
            } else {
                Log.i("MigrationUtil", "migration failed");
            }
        }
    }


    protected boolean migrateAppData(long oldVersion, long newVersion){
        return true;
    }

    protected boolean migrateLibraryData(long oldVersion, long newVersion){

        boolean successful = true;

        if(oldVersion < 1000214){
            successful = migratePreferencesToCache();
        }

        if(oldVersion < 2010001){
            successful = removeExistingTestData();
        }

        if(oldVersion < 3000002){
            successful = convertInterruptedBooleanToInteger();
        }

        // fix in case user is experiencing EXR-936
        if(oldVersion < 3000402){
            Study.getParticipant().load();
            TestCycle cycle = Study.getCurrentTestCycle();
            if(cycle!=null){
                if(cycle.getNumberOfTests()>0){
                    Study.getScheduler().scheduleNotifications(cycle, true);
                }
            }
        }

        return successful;
    }

    private boolean migratePreferencesToCache(){

        CacheManager.getInstance().removeAll();

        JsonObject json = PreferencesManager.getInstance().getObject("StateMachine", JsonObject.class);
        PreferencesManager.getInstance().remove("StateMachine");

        State state = new State();
        if(json.has("lifecycle")) {
            state.lifecycle = json.get("lifecycle").getAsInt();
        }
        if(json.has("currentPath")) {
            state.currentPath = json.get("currentPath").getAsInt();
        }
        PreferencesManager.getInstance().putObject(StateMachine.TAG_STUDY_STATE,state);

        StateCache cache = new StateCache();
        if(json.has("segments")) {
            cache.segments = PreferencesManager.getInstance().getGson().fromJson(json.get("segments"), cache.segments.getClass());
        }
        if(json.has("cache")) {
            cache.data = PreferencesManager.getInstance().getGson().fromJson(json.get("cache"), cache.data.getClass());
        }
        CacheManager.getInstance().putObject(StateMachine.TAG_STUDY_STATE_CACHE,cache);

        return true;
    }

    private boolean removeExistingTestData(){

        Participant participant = new Participant();
        participant.load();

        ParticipantState state = participant.getState();
        for(TestCycle cycle : state.testCycles) {
            for(TestSession session : cycle.getTestSessions()) {
                if(session.isOver()){
                    session.purgeData();
                }
            }
        }

        participant.save();
        return true;
    }

    private boolean convertInterruptedBooleanToInteger(){

        JsonObject json = PreferencesManager.getInstance().getObject("ParticipantState", JsonObject.class);
        if(!json.has("testCycles")){
            return true;
        }
        JsonArray cycles = json.getAsJsonArray("testCycles");
        for(JsonElement cycleElement : cycles) {
            JsonObject cycle = cycleElement.getAsJsonObject();
            if(cycle.has("days")){
                JsonArray days = cycle.getAsJsonArray("days");
                for(JsonElement dayElement : days){
                    JsonObject day = dayElement.getAsJsonObject();
                    if(day.has("sessions")){
                        JsonArray sessions = day.getAsJsonArray("sessions");
                        for(JsonElement sessionElement : sessions){
                            JsonObject session = sessionElement.getAsJsonObject();
                            if(session.has("interrupted")){
                                boolean interrupted = session.get("interrupted").getAsBoolean();
                                int integer = interrupted?1:0;
                                session.addProperty("interrupted",integer);
                            }
                        }
                    }
                }
            }
        }
        PreferencesManager.getInstance().putObject("ParticipantState", json);
        return true;
    }

}
