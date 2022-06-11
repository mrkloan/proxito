package io.fries.demo.test.cucumber.wiremock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!mock")
public class DisabledMockConfiguration {

    @Bean
    public MockServersFactory disabledMockServersFactory() {
        return scenario -> new MockServers(List.of());
    }
}
