package com.firefly.rpc.client.config;

import com.firefly.rpc.client.FireflyClientPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitClientConfiguration {
    
    @Bean
    public FireflyClientPostProcessor initClientBean() {
        return new FireflyClientPostProcessor();
    }
}
