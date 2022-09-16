package io.fries.demo.test.cucumber

import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.spring.CucumberContextConfiguration
import io.fries.demo.api.DemoApi
import io.fries.demo.test.cucumber.wiremock.MockServers
import io.fries.demo.test.cucumber.wiremock.MockServersFactory
import io.fries.demo.test.cucumber.world.World
import io.fries.demo.test.cucumber.world.scenario.ScenarioId
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.annotation.ComponentScan

@SpringBootTest(
    classes = [DemoApi::class],
    webEnvironment = WebEnvironment.DEFINED_PORT
)
@ComponentScan
@AutoConfigureMockMvc
@CucumberContextConfiguration
@ConfigurationPropertiesScan
class CucumberConfiguration(
    private val world: World,
    private val mockServersFactory: MockServersFactory
) {

    private lateinit var mockServers: MockServers

    @Before
    fun beforeEach(scenario: Scenario) {
        world.reset(ScenarioId.from(scenario))
        mockServers = mockServersFactory.invoke(world)
        mockServers.start()
    }

    @After
    fun afterEach() {
        mockServers.stop()
    }
}