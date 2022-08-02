package io.fries.demo.test.cucumber.wiremock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!mock")
public class DisabledMockConfiguration {

    @Bean
    public MockServersFactory disabledMockServersFactory() {
        return world -> MockServers.none();
    }
}
