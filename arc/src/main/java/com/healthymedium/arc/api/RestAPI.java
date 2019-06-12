package com.healthymedium.arc.api;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestAPI {

    @POST("device-heartbeat")
    Call<ResponseBody> sendHeartbeat(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("device-registration")
    Call<ResponseBody> registerDevice(@Body JsonObject body);

    @GET("get-contact-info")
    Call<ResponseBody> getContactInfo(@Query("device_id") String deviceId);

    @GET("get-session-info")
    Call<ResponseBody> getSessionInfo(@Query("device_id") String deviceId);

    @POST("signature-data")
    Call<ResponseBody> submitSignature(@Body RequestBody singatureData, @Query("device_id") String deviceId);

    @POST("submit-test")
    Call<ResponseBody> submitTest(@Query("device_id") String deviceId, @Body JsonObject test);

    @POST("submit-test-schedule")
    Call<ResponseBody> submitTestSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-wake-sleep-schedule")
    Call<ResponseBody> submitWakeSleepSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

}
