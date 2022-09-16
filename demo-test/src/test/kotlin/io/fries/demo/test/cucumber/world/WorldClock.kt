package io.fries.demo.test.cucumber.world

import java.time.ZonedDateTime

class WorldClock(
    private var clock: () -> ZonedDateTime = { ZonedDateTime.now() }
) {

    fun fixedAt(zonedDateTime: ZonedDateTime) = apply {
        clock = { zonedDateTime }
    }

    fun now(): ZonedDateTime = clock.invoke()
}