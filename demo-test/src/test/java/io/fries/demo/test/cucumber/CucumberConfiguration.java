package io.fries.demo.test.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import io.fries.demo.api.DemoApi;
import io.fries.demo.test.cucumber.wiremock.MockServers;
import io.fries.demo.test.cucumber.wiremock.MockServersFactory;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = DemoApi.class, webEnvironment = RANDOM_PORT)
@ComponentScan
@AutoConfigureMockMvc
@CucumberContextConfiguration
@EnableConfigurationProperties(WireMockProperties.class)
public class CucumberConfiguration {

    @Autowired
    private MockServersFactory mockServersFactory;

    private MockServers mockServers;

    @Before
    public void beforeEach(final Scenario scenario) {
        mockServers = mockServersFactory.createFor(scenario);
        mockServers.start();
    }

    @After
    public void afterEach() {
        mockServers.stop();
    }
}
