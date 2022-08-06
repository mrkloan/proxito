package io.fries.demo.test.cucumber.steps.when;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.When;
import io.fries.demo.test.cucumber.world.World;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicesSteps {

    @Autowired
    private World world;

    @When("a request is sent to {string}")
    public void when_a_request_is_sent_to(final String endpoint) {
        world.performGet(endpoint);
        world.tracker().assertThatAllRequestsWereRecorded();

        final var responseJson = JsonPath.parse(world.response().getBody()).read("$");
        world.filteredData(responseJson);
    }
}
