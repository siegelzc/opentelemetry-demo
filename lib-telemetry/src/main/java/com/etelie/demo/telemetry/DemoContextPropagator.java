package com.etelie.demo.telemetry;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;

import java.util.Collection;
import java.util.stream.Collectors;

public class DemoContextPropagator implements TextMapPropagator {

    public static DemoContextPropagator getInstance() {
        return INSTANCE;
    }

    private static final DemoContextPropagator INSTANCE = new DemoContextPropagator();

    private DemoContextPropagator() {
    }

    @Override
    public Collection<String> fields() {
        return DemoContextKey.REGISTRY
                .stream()
                .map(DemoContextKey::getName)
                .collect(Collectors.toList());
    }

    @Override
    public <C> void inject(Context context, C carrier, TextMapSetter<C> setter) {
        setter.set(carrier, DemoContextKey.DEMO.getName(), context.get(DemoContextKey.DEMO));
    }

    @Override
    public <C> Context extract(Context context, C carrier, TextMapGetter<C> getter) {
        String value = getter.get(carrier, DemoContextKey.DEMO.getName());
        return context.with(DemoContextKey.DEMO, value);
    }

}
