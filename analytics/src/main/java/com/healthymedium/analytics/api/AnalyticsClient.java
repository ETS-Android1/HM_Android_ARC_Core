package com.healthymedium.analytics.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthymedium.analytics.Analytics;
import com.healthymedium.analytics.AnalyticsPreferences;
import com.healthymedium.analytics.Config;
import com.healthymedium.analytics.Log;
import com.healthymedium.analytics.Parcel;
import com.healthymedium.analytics.ParcelFile;
import com.healthymedium.analytics.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnalyticsClient {

    private final static String tag = "AnalyticsClient";
    private final static String TAG_REPORT_QUEUE = "ReportQueue";
    private final static String TAG_LOG_REPORT = "LogReport";

    Gson gson;
    Retrofit retrofit;
    AnalyticsAPI service;

    List<Report> reportQueue;
    Report logReport;
    boolean uploading = false;

    public AnalyticsClient() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if(Config.Api.ENABLED) {
            clientBuilder.addInterceptor(new AnalyticsInterceptor());
        } else {
            clientBuilder.addInterceptor(new MockInterceptor());
        }
        OkHttpClient client = clientBuilder.build();

        gson = new GsonBuilder()
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .setPrettyPrinting()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Config.Api.ENDPOINT)
                .client(client)
                .build();

        service = retrofit.create(AnalyticsAPI.class);

        if(AnalyticsPreferences.contains(TAG_REPORT_QUEUE)) {
            Report[] reports = AnalyticsPreferences.getObject(TAG_REPORT_QUEUE, Report[].class);
            if(reports==null) {
                AnalyticsPreferences.remove(TAG_REPORT_QUEUE);
                reports = new Report[0];
                Log.system.i(tag, "report queue cleared");
            }
            List<Report> uploadData = Arrays.asList(reports);
            reportQueue = Collections.synchronizedList(new ArrayList<>(uploadData));
            Log.i(tag, "queue contains "+reportQueue.size()+" report(s)");
        } else {
            reportQueue = Collections.synchronizedList(new ArrayList<Report>());
        }

        if(AnalyticsPreferences.contains(TAG_LOG_REPORT)) {
            logReport = AnalyticsPreferences.getObject(TAG_LOG_REPORT, Report.class);
            if(logReport==null) {
                AnalyticsPreferences.remove(TAG_LOG_REPORT);
                Log.system.i(tag, "log report cleared");
            } else {
                Log.i(tag, "logs are queued for upload");
            }
        } else {
            logReport = null;
        }

    }

    public void submitReport(Report report) {
        Log.i(tag,"submit report");
        reportQueue.add(report);
        saveReportQueue();
        if(!uploading){
            popQueue();
        }
    }

    public void submitParcel(Report report, List<ParcelFile> files, final Analytics.Listener listener) {
        Log.i(tag,"submit parcel");

        Parcel parcel = new Parcel(gson.toJson(report),files);
        RequestBody requestBody = parcel.getRequestBody();
        if(requestBody==null){
            Log.i(tag,"on failure");
            if(listener!=null) {
                listener.onFailure();
            }
            return;
        }

        Call<ResponseBody> call = service.submitParcel(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(tag,"on response");

                if(!response.isSuccessful()){
                    Log.w(tag,"is not successful");
                    Log.w(tag,response.message());
                    if(listener!=null) {
                        listener.onFailure();
                    }
                    return;
                }
                Log.i(tag,"is successful");
                if(listener!=null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.w(tag,"on failure");
                Log.w(tag,t.getMessage());
                if(listener!=null) {
                    listener.onFailure();
                }
            }
        });
    }

    public void submitLogs(Report report, final Analytics.Listener listener) {
        Log.i(tag,"submit logs");
        logReport = report;
        AnalyticsPreferences.putObject(TAG_LOG_REPORT,logReport);

        if(uploading){
            return;
        }
        uploading = true;

        Log.i(tag,"logging paused");
        Log.system.lock();
        Log.behavior.lock();
        Log.internal.lock();

        List<ParcelFile> files = new ArrayList();
        files.add(new ParcelFile(Log.system.filename(),ParcelFile.TEXT));
        files.add(new ParcelFile(Log.behavior.filename(),ParcelFile.TEXT));
        files.add(new ParcelFile(Log.filename(),ParcelFile.TEXT));

        submitParcel(logReport, files, new Analytics.Listener() {
            @Override
            public void onSuccess() {
                AnalyticsPreferences.remove(TAG_LOG_REPORT);
                logReport = null;
                Log.system.unlock();
                Log.behavior.unlock();
                Log.internal.unlock();
                Log.i(tag,"logging resumed");
                if(listener!=null){
                    listener.onSuccess();
                }
                popQueue();
            }

            @Override
            public void onFailure() {
                Log.system.unlock();
                Log.behavior.unlock();
                Log.internal.unlock();
                Log.i(tag,"logging resumed");
                if(listener!=null){
                    listener.onFailure();
                }
                uploading = false;
            }
        });

    }

    // ---------------------------------------------------------------------------------------------

    public void popQueue() {
        Log.i(tag,"pop queue");
        if(reportQueue.size()==0) {
            Log.i(tag,"no reports left in queue");
            uploading = false;

            if(logReport!=null){
                submitLogs(logReport,null);
            }
            return;
        }
        uploading = true;

        final Report report = reportQueue.get(0);
        Log.i(tag,"sending report...\n"+gson.toJson(report));

        Call<ResponseBody> call = service.submitReport(report);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(tag,"on response");

                if(!response.isSuccessful()){
                    Log.w(tag,"is not successful");
                    Log.w(tag,response.message());
                    uploading = false;
                    return;
                }
                Log.i(tag,"is successful");
                reportQueue.remove(report);
                saveReportQueue();
                popQueue();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.w(tag,"on failure");
                Log.w(tag,t.getMessage());
                uploading = false;
            }
        });
    }

    protected void saveReportQueue(){
        Log.i(tag,"saving report queue...");
        AnalyticsPreferences.putObject(TAG_REPORT_QUEUE,reportQueue.toArray());
    }

    public void consolidateQueue(){
        int maxSize = Config.Queue.MAX_SIZE;
        int size = reportQueue.size();

        if(size<=maxSize){
            return;
        }

        Collections.reverse(reportQueue);
        for(int i=0;i<size;i++) {
            Report original = reportQueue.get(i);
            String originalStacktrace = original.getStacktrace();
            for(int j=i+1;j<size;j++){
                Report report = reportQueue.get(j);
                String stacktrace = report.getStacktrace();
                if(originalStacktrace.equals(stacktrace)){
                    original.addInstances(report.getInstances());
                    reportQueue.remove(report);
                    size--;
                    j--;
                }
            }
        }
        Collections.reverse(reportQueue);
        saveReportQueue();
    }

    public void trimQueue() {
        int maxSize = Config.Queue.MAX_SIZE;
        int size = reportQueue.size();

        if(size<=maxSize){
            return;
        }

        int excess = (size-maxSize);
        for(int i=0;i<excess;i++){
            reportQueue.remove(0);
        }
        saveReportQueue();
    }

}
