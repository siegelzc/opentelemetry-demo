package com.etelie.demo.server;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.etelie.demo"})
public class ServerApplication {

    public static final String ARTIFACT_ID = "server";

    public static void main(String[] args) {
        installJulSlf4jBridge();
        SpringApplication.run(ServerApplication.class, args);
    }

    private static void installJulSlf4jBridge() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

}
