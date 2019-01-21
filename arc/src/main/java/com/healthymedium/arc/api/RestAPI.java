package com.healthymedium.arc.api;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestAPI {

    @POST("device-registration")
    Call<ResponseBody> registerDevice(@Body JsonObject body);

    @POST("device-heartbeat")
    Call<ResponseBody> sendHeartbeat(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-wake-sleep-schedule")
    Call<ResponseBody> submitWakeSleepSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-test-schedule")
    Call<ResponseBody> submitTestSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-test")
    Call<ResponseBody> submitTest(@Query("device_id") String deviceId, @Body JsonObject test);

    @GET("get-session-info")
    Call<ResponseBody> getSessionInfo(@Query("device_id") String deviceId);

}
