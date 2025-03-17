package com.etelie.demo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    public static final String ARTIFACT_ID = "server";

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
