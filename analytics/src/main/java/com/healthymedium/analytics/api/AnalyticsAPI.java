package com.healthymedium.analytics.api;

import com.healthymedium.analytics.Report;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AnalyticsAPI {

    @POST("upload-data")
    Call<ResponseBody> submitReport(@Body Report report);

    @POST("upload-data")
    Call<ResponseBody> submitParcel(@Body RequestBody parcel);

    @GET("state-list")
    Call<ResponseBody> getStatesList();

    @GET("state-download")
    Call<ResponseBody> getStateFile(String id);

}
