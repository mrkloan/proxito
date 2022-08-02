package io.fries.demo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoApi {

    public static void main(final String[] args) {
        SpringApplication.run(DemoApi.class, args);
    }
}
