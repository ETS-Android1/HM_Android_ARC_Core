package com.healthymedium.test_suite.reporting;

import com.healthymedium.test_suite.core.TestReport;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReportingAPI {

    @POST("send-report")
    Call<ResponseBody> submitReport(@Body TestReport report);

    @POST("send-screenshot")
    Call<ResponseBody> submitScreenshot(@Body RequestBody screenshot);

}
