package io.fries.demo.test.cucumber.steps.`when`

import com.jayway.jsonpath.JsonPath
import io.cucumber.java.en.When
import io.fries.demo.test.cucumber.world.World

class ServicesSteps(private val world: World) {

    @When("a request is sent to {string}")
    fun when_a_request_is_sent_to(endpoint: String) {
        world.performGet(endpoint)
        world.tracker().assertThatAllRequestsWereRecorded()

        val responseJson = JsonPath.parse(world.response().body).read<Any>("$")
        world.filteredData(responseJson)
    }
}