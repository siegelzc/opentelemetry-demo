package com.etelie.demo.server;

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
    public OpenTelemetry initOpenTelemetry(
            SdkLoggerProvider sdkLoggerProvider,
            SdkTracerProvider sdkTracerProvider,
            SdkMeterProvider sdkMeterProvider
    ) {
        return OpenTelemetrySdk.builder()
                .setLoggerProvider(sdkLoggerProvider)
                .setTracerProvider(sdkTracerProvider)
                .setMeterProvider(sdkMeterProvider)
                .buildAndRegisterGlobal();
    }

    @Bean
    public SdkLoggerProvider openTelemetryLoggerProvider() {
        return SdkLoggerProvider.builder()
                .build();
    }

    @Bean
    public SdkTracerProvider openTelemetryTracerProvider() {
        return SdkTracerProvider.builder()
                .setSampler(Sampler.alwaysOn())
                .build();
    }

    @Bean
    public SdkMeterProvider openTelemetryMeterProvider() {
        return SdkMeterProvider.builder()
                .build();
    }

}