package com.healthymedium.test_suite.api;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.healthymedium.arc.api.DoubleTypeAdapter;
import com.healthymedium.arc.api.ItemTypeAdapterFactory;
import com.healthymedium.arc.api.RestAPI;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class MockRestClient extends com.healthymedium.arc.api.RestClient {

    NetworkBehavior networkBehavior;
    MockRetrofit mockRetrofit;

    public MockRestClient(Class type) {
        super(type);
    }

    @Override
    protected synchronized void initialize() {
        Log.i("MockRestClient","initialize");

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.url().newBuilder().build();
                request = request.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        }).build();

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .setPrettyPrinting()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.REST_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // fun stuff
        networkBehavior = NetworkBehavior.create();
        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(networkBehavior)
                .build();

        BehaviorDelegate<RestAPI> delegate = mockRetrofit.create(RestAPI.class);
       //service = new MockRestApi(delegate,gson);

        //if(type!=null){
        //    serviceExtension = retrofit.create(type);
       // }

        if(PreferencesManager.getInstance().contains("uploadQueue")) {
            List uploadData = Arrays.asList(PreferencesManager.getInstance().getObject("uploadQueue", Object[].class));
            uploadQueue = Collections.synchronizedList(new ArrayList<>(uploadData));
            Log.i("MockRestClient", "uploadQueue="+uploadQueue.toString());
        } else {
            uploadQueue = Collections.synchronizedList(new ArrayList<>());
        }
    }




}
