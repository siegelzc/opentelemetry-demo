package com.etelie.demo.server;

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

@Controller
@RequestMapping("/demo")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    private final ContextPropagators contextPropagators;
    private final TextMapGetter<HttpHeaders> httpHeadersGetter;
    private final TextMapSetter<HttpHeaders> httpHeadersSetter;
    private final Tracer tracer;
    private final Meter meter;
    private final LongCounter helloCounter;

    public DemoController(
            ContextPropagators contextPropagators,
            TextMapGetter<HttpHeaders> httpHeadersGetter,
            TextMapSetter<HttpHeaders> httpHeadersSetter,
            Tracer tracer,
            Meter meter
    ) {
        this.contextPropagators = contextPropagators;
        this.httpHeadersGetter = httpHeadersGetter;
        this.httpHeadersSetter = httpHeadersSetter;
        this.tracer = tracer;
        this.meter = meter;
        this.helloCounter = meter.counterBuilder("hello")
                .setDescription("Count number of /hello invocations")
                .build();
    }

    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public ResponseEntity<String> hello(
            @RequestParam("target") String target,
            @RequestHeader HttpHeaders headers
    ) {
        Context extractedContext = contextPropagators.getTextMapPropagator()
                .extract(Context.current(), headers, httpHeadersGetter);

        try (Scope ignored = extractedContext.makeCurrent()) {
            Span span = tracer.spanBuilder("hello")
                    .setSpanKind(SpanKind.SERVER)
                    .setAttribute("target", target)
                    .startSpan();

            String message = "Hello %s!".formatted(target);
            logger.debug(message);

            span.setAttribute("message", message);
            HttpHeaders responseHeaders = new HttpHeaders();
            contextPropagators.getTextMapPropagator()
                    .inject(Context.current(), responseHeaders, httpHeadersSetter);
            span.end();

            helloCounter.add(1);

            return ResponseEntity.ok().headers(responseHeaders).body(message);
        }
    }

}
