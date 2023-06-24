package com.oditbackend.apigw.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiGWConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
