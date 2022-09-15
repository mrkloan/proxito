package io.fries.demo.test.cucumber.steps.given

import io.cucumber.java.en.Given
import io.fries.demo.test.cucumber.world.World
import io.fries.demo.test.cucumber.world.request.QueryParameter
import java.time.ZonedDateTime

class QueryParameterSteps(private val world: World) {

    @Given("the parameter {queryParameter}")
    fun given_the_query_parameter(queryParameter: QueryParameter) {
        world.request().parameter(queryParameter)
    }

    @Given("a departure at {zonedDateTime}")
    fun given_a_departure_at(zonedDateTime: ZonedDateTime) {
        val departureDateTime = QueryParameter("departureDateTime", zonedDateTime.toString())
        given_the_query_parameter(departureDateTime)
    }
}