package com.healthymedium.arc.utilities;

import android.util.Log;

import com.google.gson.JsonObject;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.StudyState;
import com.healthymedium.arc.study.StudyStateCache;
import com.healthymedium.arc.study.StudyStateMachine;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.study.Visit;

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

        if(newLibVersion > oldLibVersion) {
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

        return successful;
    }

    private boolean migratePreferencesToCache(){

        CacheManager.getInstance().removeAll();

        JsonObject json = PreferencesManager.getInstance().getObject("StateMachine", JsonObject.class);
        PreferencesManager.getInstance().remove("StateMachine");

        StudyState state = new StudyState();
        if(json.has("lifecycle")) {
            state.lifecycle = json.get("lifecycle").getAsInt();
        }
        if(json.has("currentPath")) {
            state.currentPath = json.get("currentPath").getAsInt();
        }
        PreferencesManager.getInstance().putObject(StudyStateMachine.TAG_STUDY_STATE,state);

        StudyStateCache cache = new StudyStateCache();
        if(json.has("segments")) {
            cache.segments = PreferencesManager.getInstance().getGson().fromJson(json.get("segments"), cache.segments.getClass());
        }
        if(json.has("cache")) {
            cache.data = PreferencesManager.getInstance().getGson().fromJson(json.get("cache"), cache.data.getClass());
        }
        CacheManager.getInstance().putObject(StudyStateMachine.TAG_STUDY_STATE_CACHE,cache);

        return true;
    }

    private boolean removeExistingTestData(){

        Participant participant = new Participant();
        participant.load();

        ParticipantState state = participant.getState();
        for(Visit visit : state.visits) {
            for(TestSession session : visit.testSessions) {
                if(session.isOver()){
                    session.purgeData();
                }
            }
        }

        participant.save();
        return true;
    }

}
