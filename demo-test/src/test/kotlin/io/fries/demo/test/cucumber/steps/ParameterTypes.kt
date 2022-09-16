package io.fries.demo.test.cucumber.steps

import io.cucumber.java.ParameterType
import io.fries.demo.test.cucumber.world.World
import io.fries.demo.test.cucumber.world.request.QueryParameter
import java.time.LocalTime
import java.time.ZonedDateTime

class ParameterTypes(private val world: World) {

    @ParameterType("(.*)=(.*)")
    fun queryParameter(name: String, value: String): QueryParameter = QueryParameter(name, value)

    @ParameterType("\\d{2}:\\d{2}")
    fun zonedDateTime(time: String): ZonedDateTime = world.clock()
        .now()
        .with(LocalTime.parse(time))
}