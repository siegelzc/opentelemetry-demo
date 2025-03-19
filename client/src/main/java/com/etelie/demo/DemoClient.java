package com.etelie.demo;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DemoClient {

    private final OkHttpClient okHttpClient;

    public DemoClient(
            OkHttpClient okHttpClient
    ) {
        this.okHttpClient = okHttpClient;
    }

    public String hello(String target) {
        Call call = okHttpClient.newCall(new Request.Builder()
                .get()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host("localhost")
                        .port(1111)
                        .addPathSegments("demo/hello")
                        .addQueryParameter("target", "friend")
                        .build())
                .build());

        String responseString;
        try (Response response = call.execute()) {
            responseString = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return responseString;
    }

}
