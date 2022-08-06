package io.fries.demo.test.cucumber.steps.given;

import io.cucumber.java.en.Given;
import io.fries.demo.test.cucumber.world.World;
import io.fries.demo.test.cucumber.world.request.QueryParameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

public class QueryParameterSteps {

    @Autowired
    private World world;

    @Given("the parameter {queryParameter}")
    public void given_the_query_parameter(final QueryParameter queryParameter) {
        world.request().parameter(queryParameter);
    }

    @Given("a departure at {zonedDateTime}")
    public void given_a_departure_at(final ZonedDateTime zonedDateTime) {
        final var departureDateTime = new QueryParameter("departureDateTime", zonedDateTime.toString());
        given_the_query_parameter(departureDateTime);
    }
}
