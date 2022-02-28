package io.fries.wiremock.demo.api;

import io.fries.wiremock.demo.api.datetime.DateTimeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties(DateTimeProperties.class)
public class DemoApi {

    public static void main(final String[] args) {
        SpringApplication.run(DemoApi.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
