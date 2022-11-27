package io.fries.proxito.demo.cucumber.glue.world

import io.fries.proxito.demo.cucumber.glue.world.request.RequestBuilder
import io.fries.proxito.demo.cucumber.glue.world.request.RequestTracker
import io.fries.proxito.demo.cucumber.glue.world.request.ServerProperties
import io.fries.proxito.demo.cucumber.glue.world.scenario.ScenarioId
import org.assertj.core.api.Assertions.fail
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class World(
    private val serverProperties: ServerProperties,
    restTemplateBuilder: RestTemplateBuilder
) {
    private val restTemplate: RestTemplate = restTemplateBuilder.build()

    private lateinit var scenarioId: ScenarioId
    private lateinit var clock: WorldClock
    private lateinit var requestBuilder: RequestBuilder
    private lateinit var requestTracker: RequestTracker

    private var response: ResponseEntity<String>? = null
    private var filteredData: Any? = null

    fun reset(scenarioId: ScenarioId) {
        this.scenarioId = scenarioId

        clock = WorldClock()
        requestBuilder = RequestBuilder(serverProperties)
        requestTracker = RequestTracker()

        response = null
        filteredData = null
    }

    fun scenarioId(): ScenarioId = scenarioId

    fun clock(): WorldClock = clock

    fun request(): RequestBuilder = requestBuilder

    fun tracker(): RequestTracker = requestTracker

    fun performGet(endpoint: String) {
        try {
            val requestEntity = requestBuilder.path(endpoint).build(HttpMethod.GET)
            response = restTemplate.exchange(requestEntity, String::class.java)
        } catch (e: Exception) {
            fail(e.message, e)
        }
    }

    fun response(): ResponseEntity<String> = response ?: fail("response cannot be read before performing a request")

    fun filteredData(): Any = filteredData ?: fail("filteredData cannot be read before filtering the response")

    fun filteredData(filteredData: Any) = apply {
        this.filteredData = filteredData
    }
}