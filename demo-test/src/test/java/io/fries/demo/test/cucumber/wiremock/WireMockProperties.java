package io.fries.demo.test.cucumber.wiremock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Set;

@ConstructorBinding
@ConfigurationProperties(prefix = "demo.wiremock")
public record WireMockProperties(Set<WireMockServerProperties> servers) {

    public record WireMockServerProperties(
            String partner,
            int port,
            String endpoint,
            String pattern,
            String replacement
    ) {
    }
}
