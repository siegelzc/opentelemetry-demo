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
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.ServiceAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class OpentelemetryConfig {

    @Bean
    public OpenTelemetry opentelemetry() {
        Resource resource = resource();
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider(resource))
                .setMeterProvider(meterProvider(resource))
                .setLoggerProvider(loggerProvider(resource))
                .build();
        GlobalOpenTelemetry.set(openTelemetry); // This or the combined `buildAndRegisterGlobal` is required
        return openTelemetry;
    }

    /**
     * <blockquote cite="https://opentelemetry.io/docs/languages/java/sdk/#resource">
     * "An application should associate the same resource with SdkTracerProvider, SdkMeterProvider, SdkLoggerProvider."
     * </blockquote>
     */
    public Resource resource() {
        return Resource.getDefault()
                .toBuilder()
                .put(ServiceAttributes.SERVICE_NAME, ServerApplication.ARTIFACT_ID)
                .build();
    }

    public SdkTracerProvider tracerProvider(Resource resource) {
        SpanExporter spanExporter = LoggingSpanExporter.create();
        SpanProcessor spanProcessor = SpanProcessor.composite(
                SimpleSpanProcessor.builder(spanExporter).build()
        );

        return SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(spanProcessor)
                .build();
    }

    private SdkMeterProvider meterProvider(Resource resource) {
        MetricExporter metricExporter = LoggingMetricExporter.create();
        MetricReader metricReader = PeriodicMetricReader.builder(metricExporter).build();
        return SdkMeterProvider.builder()
                .setResource(resource)
                .registerMetricReader(metricReader)
                .build();
    }

    private SdkLoggerProvider loggerProvider(Resource resource) {
        return SdkLoggerProvider.builder()
                .setResource(resource)
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
