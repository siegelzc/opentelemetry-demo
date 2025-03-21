package com.etelie.demo.telemetry;

import io.opentelemetry.context.ContextKey;

import java.util.ArrayList;
import java.util.List;

public class DemoContextKey<T> implements ContextKey<T> {

    public static final List<DemoContextKey<? extends Object>> REGISTRY = new ArrayList<>();
    public static final DemoContextKey<String> DEMO = new DemoContextKey<>("demo");

    private final String name;

    private DemoContextKey(String name) {
        this.name = name;

        REGISTRY.add(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
