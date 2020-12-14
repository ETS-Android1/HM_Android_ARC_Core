package com.healthymedium.analytics.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String uri = request.url().uri().toString();
        return chain.proceed(request)
                .newBuilder()
                .code(200)
                .body(ResponseBody.create(MediaType.parse("application/json"),""))
                .build();
    }

}
