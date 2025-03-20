package com.etelie.demo;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.etelie.demo"})
public class ClientApplication {

    public static final String ARTIFACT_ID = "client";

    public static void main(String[] args) {
        installJulSlf4jBridge();
        SpringApplication.run(ClientApplication.class, args);
    }

    private static void installJulSlf4jBridge() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

}
