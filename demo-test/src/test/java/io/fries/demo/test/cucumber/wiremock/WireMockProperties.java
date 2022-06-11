package io.fries.demo.test.cucumber.wiremock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Set;
import java.util.regex.Pattern;

@ConstructorBinding
@ConfigurationProperties(prefix = "demo.wiremock")
public record WireMockProperties(Set<WireMockServerProperties> servers) {

    public record WireMockServerProperties(
            String partner,
            int port,
            String endpoint,
            WireMockTransformersProperties transformers
    ) {
    }

    public record WireMockTransformersProperties(
            WireMockTransformerProperties request,
            WireMockTransformerProperties response
    ) {
    }

    public record WireMockTransformerProperties(
            Pattern pattern,
            String replacement
    ) {
    }
}
