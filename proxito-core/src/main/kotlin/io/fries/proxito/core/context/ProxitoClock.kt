package io.fries.proxito.core.context

import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * The interface for the global time supplier bean used in the Proxito ecosystem.
 * The default `UtcClock` implementation returns the current UTC time.
 *
 * It can be mocked by overriding the primary bean definition in a test class:
 *
 * ```kotlin
 * @TestConfiguration
 * class CustomConfiguration {
 *     @Bean
 *     @Primary
 *     fun unixClock() = ProxitoClock { ZonedDateTime.parse("1970-01-01T00:00:00Z") }
 * }
 * ```
 */
fun interface ProxitoClock {
    fun now(): ZonedDateTime
}

@Component
class UtcClock : ProxitoClock {
    override fun now(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
}
