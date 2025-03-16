package com.etelie.demo.server;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    public SdkLoggerProvider loggerProvider() {
        return SdkLoggerProvider.builder()
                .build();
    }

    public SdkTracerProvider tracerProvider() {
        return SdkTracerProvider.builder()
                .setSampler(Sampler.alwaysOn())
                .build();
    }

    public SdkMeterProvider meterProvider() {
        return SdkMeterProvider.builder()
                .build();
    }

}