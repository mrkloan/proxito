package io.fries.demo.test.cucumber.wiremock

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!mock")
class DisabledMockConfiguration {

    @Bean
    fun disabledMockServersFactory(): MockServersFactory = { MockServers() }
}