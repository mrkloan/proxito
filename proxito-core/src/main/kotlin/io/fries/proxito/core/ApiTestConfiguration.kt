package io.fries.proxito.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZonedDateTime

@Configuration
class ApiTestConfiguration {

    /**
     * The supplier method providing the current time.
     *
     * It can be mocked by overriding the bean definition in a test class:
     *
     * ```kotlin
     * @TestConfiguration
     * class CustomConfiguration {
     *     @Bean
     *     @Primary
     *     fun unixClock() = { ZonedDateTime.parse("1970-01-01T00:00:00Z") }
     * }
     * ```
     */
    @Bean
    fun clock(): () -> ZonedDateTime = ZonedDateTime::now
}