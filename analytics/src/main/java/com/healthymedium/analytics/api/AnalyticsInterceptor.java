package com.healthymedium.analytics.api;

import com.healthymedium.analytics.Config;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnalyticsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("API-Version", Config.Api.VERSION)
                .header("X-API-Key", Config.Api.KEY)
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }

}
