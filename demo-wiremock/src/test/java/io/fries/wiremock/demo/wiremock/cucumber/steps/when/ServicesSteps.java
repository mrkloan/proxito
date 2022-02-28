package io.fries.wiremock.demo.wiremock.cucumber.steps.when;

import io.cucumber.java.en.When;
import io.fries.wiremock.demo.wiremock.cucumber.world.World;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicesSteps {

    @Autowired
    private World world;

    @When("a request is sent to {string}")
    public void when_a_request_is_sent_to(final String endpoint) {
        world.performGet(endpoint);
    }
}
