package com.example.demo;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DemoClient {

    private static final Logger logger = LoggerFactory.getLogger(DemoClient.class);

    public DemoClient(
            OkHttpClient okHttpClient
    ) {
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

        try (Response response = call.execute()) {
            logger.info(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
