package com.etelie.demo.server;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class OpentelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setLoggerProvider(loggerProvider())
                .setTracerProvider(tracerProvider())
                .setMeterProvider(meterProvider())
                .build();
        GlobalOpenTelemetry.set(openTelemetry);
        return openTelemetry;
    }

    private SdkLoggerProvider loggerProvider() {
        return SdkLoggerProvider.builder()
                .build();
    }

    private SdkTracerProvider tracerProvider() {
        return SdkTracerProvider.builder()
                .setSampler(Sampler.alwaysOn())
                .build();
    }

    private SdkMeterProvider meterProvider() {
        return SdkMeterProvider.builder()
                .build();
    }

    @Bean
    public ContextPropagators contextPropagators() {
        return ContextPropagators.create(
                TextMapPropagator.composite(
                        W3CTraceContextPropagator.getInstance(),
                        W3CBaggagePropagator.getInstance()
                )
        );
    }

    @Bean
    public TextMapGetter<HttpHeaders> httpHeadersGetter() {
        return new TextMapGetter<>() {
            @Override
            public Iterable<String> keys(HttpHeaders carrier) {
                return carrier.keySet();
            }

            @Override
            public String get(HttpHeaders carrier, String key) {
                return carrier.asSingleValueMap().get(key);
            }
        };
    }

    @Bean
    public TextMapSetter<HttpHeaders> httpHeadersSetter() {
        //noinspection Convert2Lambda,Anonymous2MethodRef
        return new TextMapSetter<>() {
            @Override
            public void set(HttpHeaders httpHeaders, String headerName, String headerValue) {
                httpHeaders.set(headerName, headerValue);
            }
        };
    }

    @Bean
    public Tracer tracer(
            OpenTelemetry openTelemetry
    ) {
        return openTelemetry.getTracer(ServerApplication.class.getPackageName());
    }

}
