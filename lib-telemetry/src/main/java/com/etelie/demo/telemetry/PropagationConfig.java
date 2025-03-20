package com.etelie.demo.telemetry;

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class PropagationConfig {

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

}
