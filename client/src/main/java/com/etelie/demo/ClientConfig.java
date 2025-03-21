package com.etelie.demo;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    public PeriodicExecutor helloExecutor(
            DemoClient demoClient
    ) {
        return new PeriodicExecutor(5000, () -> {
            String response = demoClient.scoped(String.class, demoClient::hello, "friend");
            log.debug("HELLO RESPONSE:\n{}", response);
        });
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

}
