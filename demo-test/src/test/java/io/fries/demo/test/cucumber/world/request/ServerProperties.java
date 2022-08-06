package io.fries.demo.test.cucumber.world.request;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "demo.test.server")
public record ServerProperties(int port) {
}
