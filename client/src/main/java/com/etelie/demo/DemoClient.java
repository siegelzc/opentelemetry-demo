package com.etelie.demo;

import com.etelie.demo.telemetry.DemoContextKey;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapSetter;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Function;

@Component
public class DemoClient {

    private final OkHttpClient okHttpClient;
    private final ContextPropagators contextPropagators;
    private final TextMapSetter<Headers.Builder> okhttpHeadersBuilderSetter;

    public DemoClient(
            OkHttpClient okHttpClient,
            ContextPropagators contextPropagators,
            TextMapSetter<Headers.Builder> okhttpHeadersBuilderSetter
    ) {
        this.okHttpClient = okHttpClient;
        this.contextPropagators = contextPropagators;
        this.okhttpHeadersBuilderSetter = okhttpHeadersBuilderSetter;
    }

    public <R, A extends Object> R scoped(
            @SuppressWarnings("unused") Class<R> returnType,
            Function<A, R> function,
            A argument
    ) {
        Context preparedContext = Context.current()
                .with(ContextKey.named("testKey"), "testValue");
        try (Scope ignored = preparedContext.makeCurrent()) {
            return function.apply(argument);
        }
    }

    public String hello(String target) {
        Headers.Builder headersBuilder = new Headers.Builder();
        contextPropagators.getTextMapPropagator().inject(
                Context.current().with(DemoContextKey.DEMO, "some value"),
                headersBuilder,
                okhttpHeadersBuilderSetter);

        Call call = okHttpClient.newCall(new Request.Builder()
                .get()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host("localhost")
                        .port(1111)
                        .addPathSegments("demo/hello")
                        .addQueryParameter("target", "friend")
                        .build())
                .headers(headersBuilder.build())
                .build());

        try (Response response = call.execute()) {
            Headers headers = response.headers();
            return "%sBODY: %s".formatted(headers.toString(), response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
