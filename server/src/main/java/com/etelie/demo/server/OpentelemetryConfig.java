package com.etelie.demo.server;

import com.etelie.demo.telemetry.OpentelemetryFactory;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class OpentelemetryConfig {

    private static final String INSTRUMENTATION_SCOPE_NAME = OpentelemetryConfig.class.getPackageName();

    @Bean
    public OpenTelemetry openTelemetry() {
        return OpentelemetryFactory.create(ServerApplication.ARTIFACT_ID);
    }

    @Bean
    public Tracer tracer(
            OpenTelemetry openTelemetry
    ) {
        return openTelemetry.getTracer(INSTRUMENTATION_SCOPE_NAME);
    }

    @Bean
    public Meter meter(
            OpenTelemetry openTelemetry
    ) {
        return openTelemetry.getMeter(INSTRUMENTATION_SCOPE_NAME);
    }

    @Bean
    public Logger logger(
            OpenTelemetry openTelemetry
    ) {
        return openTelemetry.getLogsBridge().get(INSTRUMENTATION_SCOPE_NAME);
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

}
