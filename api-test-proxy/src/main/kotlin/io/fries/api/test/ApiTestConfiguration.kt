package io.fries.api.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZonedDateTime

@Configuration
class ApiTestConfiguration {

    @Bean
    fun clock(): () -> ZonedDateTime = ZonedDateTime::now
}