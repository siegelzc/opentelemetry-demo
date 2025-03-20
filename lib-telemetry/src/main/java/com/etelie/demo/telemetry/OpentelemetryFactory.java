package com.etelie.demo.telemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
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

public final class OpentelemetryFactory {

    private OpentelemetryFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static OpenTelemetry create(String serviceName) {
        Resource resource = resource(serviceName);
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
    private static Resource resource(String serviceName) {
        return Resource.getDefault()
                .toBuilder()
                .put(ServiceAttributes.SERVICE_NAME, serviceName)
                .build();
    }

    private static SdkTracerProvider tracerProvider(Resource resource) {
        SpanExporter spanExporter = LoggingSpanExporter.create();
        SpanProcessor spanProcessor = SpanProcessor.composite(
                SimpleSpanProcessor.builder(spanExporter).build()
        );

        return SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(spanProcessor)
                .build();
    }

    private static SdkMeterProvider meterProvider(Resource resource) {
        MetricExporter metricExporter = LoggingMetricExporter.create();
        MetricReader metricReader = PeriodicMetricReader.builder(metricExporter).build();
        return SdkMeterProvider.builder()
                .setResource(resource)
                .registerMetricReader(metricReader)
                .build();
    }

    private static SdkLoggerProvider loggerProvider(Resource resource) {
        LogRecordExporter logRecordExporter = LogRecordExporter.composite(SystemOutLogRecordExporter.create()); // Empty composition creates NoopLogRecordExporter
        LogRecordProcessor logRecordProcessor = LogRecordProcessor.composite(
                SimpleLogRecordProcessor.create(logRecordExporter)
        );
        return SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(logRecordProcessor)
                .build();
    }

}
