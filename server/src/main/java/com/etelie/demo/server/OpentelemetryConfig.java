import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

public class OpentelemetryConfig {

    public static OpenTelemetry initOpenTelemetry() {
        // Create a span exporter (e.g., OTLP exporter for Jaeger, Tempo, etc.)
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317") // OTLP endpoint
                .build();

        // Create a span processor
        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(spanExporter).build();

        // Create a tracer provider
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .build();

        // Build the OpenTelemetry instance
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }
}