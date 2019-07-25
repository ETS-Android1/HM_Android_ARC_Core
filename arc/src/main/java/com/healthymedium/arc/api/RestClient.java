package com.healthymedium.arc.api;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.healthymedium.arc.api.models.VerificationCodeRequest;
import com.healthymedium.arc.utilities.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.healthymedium.arc.api.models.CachedObject;
import com.healthymedium.arc.api.models.DeviceRegistration;
import com.healthymedium.arc.api.models.ExistingData;
import com.healthymedium.arc.api.models.Heartbeat;
import com.healthymedium.arc.api.models.CachedSignature;
import com.healthymedium.arc.api.models.SessionInfo;
import com.healthymedium.arc.api.models.TestSchedule;
import com.healthymedium.arc.api.models.TestScheduleSession;
import com.healthymedium.arc.api.models.TestSubmission;
import com.healthymedium.arc.api.models.WakeSleepData;
import com.healthymedium.arc.api.models.WakeSleepSchedule;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.CircadianRhythm;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Visit;
import com.healthymedium.arc.utilities.CacheManager;
import com.healthymedium.arc.utilities.VersionUtil;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.time.JodaUtil;
import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.healthymedium.arc.study.Study.TAG_CONTACT_INFO;

@SuppressWarnings("unchecked")
public class RestClient <Api>{

    private Class<Api> type;
    private RestAPI service;
    private Api serviceExtension;
    protected Gson gson;
    protected Retrofit retrofit;

    protected List<Object> uploadQueue = Collections.synchronizedList(new ArrayList<>());
    private UploadListener uploadListener = null;
    protected boolean uploading = false;

    public RestClient(Class<Api> type) {
        this.type = type;
        initialize();
    }

    protected synchronized void initialize() {
        Log.i("RestClient","initialize");

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.url().newBuilder().build();
                request = request.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = clientBuilder.build();

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .setPrettyPrinting()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Config.REST_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(RestAPI.class);
        if(type!=null){
            serviceExtension = retrofit.create(type);
        }

        if(PreferencesManager.getInstance().contains("uploadQueue")) {
            List uploadData = Arrays.asList(PreferencesManager.getInstance().getObject("uploadQueue", Object[].class));
            uploadQueue = Collections.synchronizedList(new ArrayList<>(uploadData));
            Log.i("RestClient", "uploadQueue="+uploadQueue.toString());
        } else {
            uploadQueue = Collections.synchronizedList(new ArrayList<>());
        }
    }

    public RestAPI getService() {
        return service;
    }

    public Api getServiceExtension() {
        return serviceExtension;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    // public calls --------------------------------------------------------------------------------

    public void registerDevice(final DeviceRegistration registration, final Listener listener){
        if(Config.REST_BLACKHOLE) {
            return;
        }

        CallbackChain chain = new CallbackChain();

        Call<ResponseBody> call = getService().registerDevice(serialize(registration));
        chain.addLink(call);

        if(Config.CHECK_CONTACT_INFO){
            Call<ResponseBody> contactInfo = getService().getContactInfo(Device.getId());
            chain.addLink(contactInfo,contactListener);
        }

        if(Config.CHECK_SESSION_INFO) {
            Call<ResponseBody> sessionInfo = getService().getSessionInfo(Device.getId());
            chain.addLink(sessionInfo, sessionListener);
        }

        chain.execute(listener);
    }

    public void registerDevice(String participantId, String authorizationCode, boolean existingUser, final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","registerDevice(existingUser="+existingUser+")");

        DeviceRegistration registration = new DeviceRegistration();
        registration.app_version = VersionUtil.getAppVersionName();
        registration.device_id = Device.getId();
        registration.device_info = Device.getInfo();
        registration.participant_id = participantId;
        registration.authorization_code = authorizationCode;
        if(existingUser){
            registration.override = Boolean.TRUE;
        }
        registerDevice(registration,listener);
    }

    public void requestVerificationCode(Listener listener){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","requestVerificationCode");

        VerificationCodeRequest request = new VerificationCodeRequest();
        request.setArcId(Study.getParticipant().getId());
        JsonObject json = serialize(request);

        Call<ResponseBody> call = getService().requestVerificationCode(json);
        call.enqueue(createCallback(listener));
    }

    public void getSessionInfo(final Listener listener ){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","getSessionInfo()");
        Call<ResponseBody> call = getService().getSessionInfo(Device.getId());
        call.enqueue(createCallback(listener));
    }

    public void sendHeartbeat(String participantId, final Listener listener ){
        if(Config.REST_BLACKHOLE || !Config.REST_HEARTBEAT) {
            return;
        }
        Log.i("RestClient","sendHeartbeat()");

        Heartbeat heartbeat = new Heartbeat();
        heartbeat.app_version = VersionUtil.getAppVersionName();
        heartbeat.device_id = Device.getId();
        heartbeat.device_info = Device.getInfo();
        heartbeat.participant_id = participantId;

        JsonObject json = serialize(heartbeat);
        Call<ResponseBody> call = getService().sendHeartbeat(Device.getId(), json);
        call.enqueue(createCallback(listener));
    }

    public void submitWakeSleepSchedule(){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","submitWakeSleepSchedule()");

        Participant participant = Study.getParticipant();
        CircadianClock clock = participant.getCircadianClock();

        WakeSleepSchedule schedule = new WakeSleepSchedule();
        schedule.app_version = VersionUtil.getAppVersionName();
        schedule.device_id = Device.getId();
        schedule.device_info = Device.getInfo();
        schedule.participant_id = participant.getId();
        schedule.wakeSleepData = new ArrayList<>();

        for(CircadianRhythm rhythm : clock.getRhythms()) {
            WakeSleepData data = new WakeSleepData();
            data.bed = rhythm.getBedTime().toString("h:mm a");
            data.wake = rhythm.getWakeTime().toString("h:mm a");
            data.weekday = rhythm.getWeekday();
            schedule.wakeSleepData.add(data);
        }

        if(uploading){
            uploadQueue.add(schedule);
            saveUploadQueue();
        } else {
            markUploadStarted();
            JsonObject json = serialize(schedule);
            Call<ResponseBody> call = getService().submitWakeSleepSchedule(Device.getId(), json);
            call.enqueue(createDataCallback(schedule));
        }
    }

    public void submitTestSchedule(){
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","submitTestSchedule()");

        Participant participant = Study.getParticipant();
        ParticipantState state = participant.getState();

        TestSchedule schedule = new TestSchedule();
        schedule.app_version = VersionUtil.getAppVersionName();
        schedule.device_id = Device.getId();
        schedule.device_info = Device.getInfo();
        schedule.participant_id = participant.getId();
        schedule.sessions = new ArrayList<>();

        for(Visit visit : state.visits){
            for(TestSession session : visit.getTestSessions()){
                TestScheduleSession scheduleSession = new TestScheduleSession();
                scheduleSession.session = session.getIndex()%visit.getNumberOfTests(session.getDayIndex());
                scheduleSession.session_id = String.valueOf(session.getId());
                scheduleSession.session_date = JodaUtil.toUtcDouble(session.getScheduledTime());
                scheduleSession.week = Weeks.weeksBetween(state.visits.get(0).getScheduledStartDate(),visit.getScheduledStartDate()).getWeeks();
                scheduleSession.day = session.getDayIndex();
                scheduleSession.types = new ArrayList<>();
                scheduleSession.types.add("cognitive");
                schedule.sessions.add(scheduleSession);
            }
        }

        if(uploading){
            uploadQueue.add(schedule);
            saveUploadQueue();
        } else {
            markUploadStarted();
            JsonObject json = serialize(schedule);
            Call<ResponseBody> call = getService().submitTestSchedule(Device.getId(), json);
            call.enqueue(createDataCallback(schedule));
        }
    }

    public void submitSignature(Bitmap bitmap) {
        Log.i("RestClient","submitSignature");
        if(Config.REST_BLACKHOLE) {
            return;
        }

        String key = "signature_" + DateTime.now().getMillis();
        CacheManager.getInstance().putBitmap(key,bitmap,100);

        CachedSignature signature = new CachedSignature();
        signature.filename = key;
        signature.participant_id = Study.getParticipant().getId();
        signature.session_id = String.valueOf(Study.getCurrentTestSession().getId());

        if(uploading) {
            Log.i("RestClient","adding signature to upload queue");
            uploadQueue.add(signature);
            saveUploadQueue();
        } else {
            Log.i("RestClient","uploading signature now");
            markUploadStarted();
            Call<ResponseBody> call = getService().submitSignature(signature.getRequestBody(),Device.getId());
            call.enqueue(createCachedCallback(signature));
        }
    }

    public void submitTest(TestSession session) {
        if(Config.REST_BLACKHOLE) {
            return;
        }
        TestSubmission test  = createTestSubmission(session);
        submitTest(test);
    }

    public void submitTest(TestSubmission test) {
        if(Config.REST_BLACKHOLE) {
            return;
        }
        Log.i("RestClient","submitTest(id="+test.session_id+")");
        if(uploading){
            uploadQueue.add(test);
            saveUploadQueue();
        } else {
            markUploadStarted();
            JsonObject json = serialize(test);
            Call<ResponseBody> call = getService().submitTest(Device.getId(), json);
            call.enqueue(createDataCallback(test));
        }
    }

    // utility functions ---------------------------------------------------------------------------

    protected TestSubmission createTestSubmission(TestSession session)
    {
        TestSubmission test  = new TestSubmission();
        test.app_version = VersionUtil.getAppVersionName();
        test.device_id = Device.getId();
        test.device_info = Device.getInfo();
        test.participant_id = Study.getParticipant().getId();

        test.session_id = String.valueOf(session.getId());
        test.id = test.session_id;
        test.session_date = JodaUtil.toUtcDouble(session.getScheduledTime());
        if(session.getStartTime()!=null){
            test.start_time = JodaUtil.toUtcDouble(session.getStartTime());
        }
        test.day = session.getDayIndex();

        DateTime scheduledTime = session.getScheduledTime();
        List<Visit> visits =  Study.getParticipant().getState().visits;

        for(Visit visit : visits) {
            if (scheduledTime.isBefore(visit.getActualEndDate()) && scheduledTime.isAfter(visit.getActualStartDate())) {
                test.week = Weeks.weeksBetween(visits.get(0).getScheduledStartDate(),visit.getScheduledStartDate()).getWeeks();
                test.session = session.getIndex()%visit.getNumberOfTests(session.getDayIndex());
                break;
            }
        }

        test.missed_session = session.wasMissed() ? 1 : 0;
        test.finished_session = session.wasFinished() ? 1 : 0;
        test.interrupted = session.wasInterrupted() ? 1 : 0;
        test.tests = session.getTestData();

        return test;
    }

    // callback creation ---------------------------------------------------------------------------

    protected Callback createCallback(@Nullable final Listener listener) {
        Log.i("RestClient","createCallback");
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {
                if(listener==null){
                    return;
                }

                RestResponse response = RestResponse.fromRetrofitResponse(retrofitResponse);
                Log.i("RestClient",gson.toJson(response));
                if(response.successful){
                    Log.i("RestClient","onSuccess");
                    listener.onSuccess(response);
                } else {
                    Log.i("RestClient","onFailure");
                    listener.onFailure(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if(listener==null){
                    return;
                }

                RestResponse response = RestResponse.fromRetrofitFailure(throwable);
                Log.i("RestClient",gson.toJson(response));
                Log.i("RestClient","onFailure");
                listener.onFailure(response);
            }
        };
    }

    protected Callback createDataCallback(final Object object) {
        Log.i("RestClient","createDataCallback");
        return createCallback(new Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                Log.i("RestClient","data callback success");
                if(uploadQueue.contains(object)) {
                    uploadQueue.remove(object);
                    saveUploadQueue();
                }
                popQueue();
            }

            @Override
            public void onFailure(RestResponse response) {
                Log.i("RestClient","data callback failure");
                if(!uploadQueue.contains(object)){
                    uploadQueue.add(object);
                    saveUploadQueue();
                }
                markUploadStopped();
            }
        });
    }

    protected Callback createCachedCallback(final CachedObject object) {
        Log.i("RestClient","createCachedCallback");
        return createCallback(new Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                Log.i("RestClient","cached callback success");
                if(uploadQueue.contains(object)) {
                    uploadQueue.remove(object);
                    saveUploadQueue();
                }
                CacheManager.getInstance().remove(object.filename);
                popQueue();
            }

            @Override
            public void onFailure(RestResponse response) {
                Log.i("RestClient","cached callback failure");
                if(!uploadQueue.contains(object)){
                    uploadQueue.add(object);
                    saveUploadQueue();
                }
                markUploadStopped();
            }
        });
    }

    CallbackChain.Listener contactListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful) {
                if (response.optional.has("contact_info")) {
                    JsonObject contactJson = response.optional.get("contact_info").getAsJsonObject();
                    if (contactJson.has("phone")) {
                        PreferencesManager.getInstance().putString(TAG_CONTACT_INFO, contactJson.get("phone").getAsString());
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    CallbackChain.Listener sessionListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful){

                JsonElement firstElement = response.optional.get("first_test");
                JsonElement latestElement = response.optional.get("latest_test");
                if(firstElement.isJsonNull() || latestElement.isJsonNull()){
                    return true;
                }

                ExistingData existingData = new ExistingData();
                existingData.first_test = gson.fromJson(firstElement,SessionInfo.class);
                existingData.latest_test = gson.fromJson(latestElement,SessionInfo.class);

                chain.setPersistentObject(existingData);

                Call<ResponseBody> wakeSleepSchedule = getService().getWakeSleepSchedule(Device.getId());
                chain.addLink(wakeSleepSchedule, wakeSleepListener);

                return true;
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    CallbackChain.Listener wakeSleepListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful){

                JsonElement wakeSleepJson = response.optional.get("wake_sleep_schedule");
                if(wakeSleepJson.isJsonNull()){
                    return false;
                }

                ExistingData existingData = (ExistingData) chain.getPersistentObject();
                existingData.wake_sleep_schedule = gson.fromJson(wakeSleepJson,WakeSleepSchedule.class);

                Call<ResponseBody> testScheduleCall = service.getTestSchedule(Device.getId());
                chain.addLink(testScheduleCall,testScheduleListener);

                return true;
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };

    CallbackChain.Listener testScheduleListener = new CallbackChain.Listener() {
        @Override
        public boolean onResponse(CallbackChain chain, RestResponse response) {
            if(response.successful){

                JsonElement testSessionJson = response.optional.get("test_schedule");
                if(testSessionJson.isJsonNull()){
                    return false;
                }

                ExistingData existingData = (ExistingData) chain.getPersistentObject();
                existingData.test_schedule = gson.fromJson(testSessionJson,TestSchedule.class);

                if(!existingData.isValid()){
                    return false;
                }

                ParticipantState state = Study.getScheduler().getExistingParticipantState(existingData);
                Study.getParticipant().setState(state);
                Study.getScheduler().scheduleNotifications(Study.getCurrentVisit(), false);

                return true;
            }
            return false;
        }

        @Override
        public boolean onFailure(CallbackChain chain, RestResponse response) {
            return false;
        }
    };


    // upload queue --------------------------------------------------------------------------------

    public void popQueue() {
        if(uploadQueue.size()>0){
            Object object = uploadQueue.get(0);
            JsonObject json = serialize(object);
            String deviceId = Device.getId();
            Call<ResponseBody> call = null;

            if(TestSchedule.class.isInstance(object)) {
                call = getService().submitTestSchedule(deviceId,json);
            } else if(WakeSleepSchedule.class.isInstance(object)) {
                call = getService().submitWakeSleepSchedule(deviceId,json);
            } else if(TestSubmission.class.isInstance(object)) {
                call = getService().submitTest(deviceId,json);
            } else if(CachedSignature.class.isInstance(object)) {
                RequestBody requestBody = ((CachedSignature)object).getRequestBody();
                call = getService().submitSignature(requestBody,deviceId);
            }

            if(call!=null) {
                Log.i("RestClient", "popQueue("+object.getClass().getName()+")");
                markUploadStarted();
                if(CachedObject.class.isAssignableFrom(object.getClass())){
                    call.enqueue(createCachedCallback((CachedObject) object));
                } else {
                    call.enqueue(createDataCallback(object));
                }

            } else {
                uploadQueue.remove(object);
                saveUploadQueue();
                popQueue();
            }
        } else {
            markUploadStopped();
        }
    }

    protected JsonObject serialize(Object object){
        JsonElement element = gson.toJsonTree(object);
        Log.v("RestClient",gson.toJson(object));
        return element.getAsJsonObject();
    }

    protected void saveUploadQueue(){
        Log.i("RestClient","saveUploadQueue");
        PreferencesManager.getInstance().putObject("uploadQueue",uploadQueue.toArray());
        Log.i("RestClient",uploadQueue.toString());
    }

    protected void markUploadStarted(){
        if(!uploading) {
            Log.i("RestClient","upload started");
            uploading = true;
            if (uploadListener != null) {
                uploadListener.onStart();
            }
        }
    }

    private void markUploadStopped(){
        if(uploading) {
            Log.i("RestClient","upload stopped");
            uploading = false;
            if (uploadListener != null) {
                uploadListener.onStop();
            }
        }
    }

    public boolean isUploadQueueEmpty(){
        return uploadQueue.size()==0;
    }

    public void setUploadListener(UploadListener listener){
        uploadListener = listener;
    }

    public void removeUploadListener(){
        uploadListener = null;
    }

    // listener interfaces -------------------------------------------------------------------------

    public interface Listener{
        void onSuccess(RestResponse response);
        void onFailure(RestResponse response);
    }

    public interface UploadListener{
        void onStart();
        void onStop();
    }
}
