package com.etelie.demo.server;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.function.Supplier;

@Controller
@RequestMapping("/demo")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    private final ContextPropagators contextPropagators;
    private final TextMapGetter<HttpHeaders> httpHeadersGetter;
    private final TextMapSetter<HttpHeaders> httpHeadersSetter;
    private final Tracer tracer;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Meter meter;
    private final LongCounter helloCounter;
    private final io.opentelemetry.api.logs.Logger logger;

    public DemoController(
            ContextPropagators contextPropagators,
            TextMapGetter<HttpHeaders> httpHeadersGetter,
            TextMapSetter<HttpHeaders> httpHeadersSetter,
            Tracer tracer,
            Meter meter,
            io.opentelemetry.api.logs.Logger logger
    ) {
        this.contextPropagators = contextPropagators;
        this.httpHeadersGetter = httpHeadersGetter;
        this.httpHeadersSetter = httpHeadersSetter;
        this.tracer = tracer;
        this.meter = meter;
        this.logger = logger;

        this.helloCounter = meter.counterBuilder("hello")
                .setDescription("Count number of /hello invocations")
                .build();
    }

    private <T extends Object> ResponseEntity<T> scoped(HttpHeaders headers, Supplier<ResponseEntity<T>> supplier) {
        Context extractedContext = contextPropagators.getTextMapPropagator()
                .extract(Context.current(), headers, httpHeadersGetter);

        try (Scope ignored = extractedContext.makeCurrent()) {
            return supplier.get();
        }
    }

    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public ResponseEntity<String> hello(
            @RequestParam("target") String target,
            @RequestHeader HttpHeaders headers
    ) {
        return scoped(headers, () -> {
            Span span = tracer.spanBuilder("hello")
                    .setSpanKind(SpanKind.SERVER)
                    .setAttribute("target", target)
                    .startSpan();

            String message = "Hello %s!".formatted(target);
            log.debug(message);

            logger.logRecordBuilder() // Note: This is for demonstration only. The Log Bridge API shouldn't be used like this.
                    .setBody(message)
                    .setAttribute(AttributeKey.stringKey("target"), target)
                    .setSeverity(Severity.DEBUG)
                    .emit();
            span.setAttribute("message", message)
                    .end();
            helloCounter.add(1);

            HttpHeaders responseHeaders = new HttpHeaders();
            contextPropagators.getTextMapPropagator()
                    .inject(Context.current(), responseHeaders, httpHeadersSetter);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(message);
        });
    }

}
