package com.etelie.demo;

import com.etelie.demo.telemetry.OpentelemetryFactory;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.semconv.ServiceAttributes;
import okhttp3.Headers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpentelemetryConfig {

    private static final String INSTRUMENTATION_SCOPE_NAME = OpentelemetryConfig.class.getPackageName();

    @Bean
    public OpenTelemetry openTelemetry() {
        return OpentelemetryFactory.create(ClientApplication.ARTIFACT_ID);
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
    public TextMapSetter<Headers.Builder> okhttpHeadersBuilderSetter() {
        return new TextMapSetter<Headers.Builder>() {
            @Override
            public void set(Headers.Builder carrier, String key, String value) {
                carrier.set(key, value);
            }
        };
    }

}
