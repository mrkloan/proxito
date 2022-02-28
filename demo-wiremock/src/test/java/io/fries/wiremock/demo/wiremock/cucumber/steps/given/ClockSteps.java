package io.fries.wiremock.demo.wiremock.cucumber.steps.given;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.fries.wiremock.demo.wiremock.cucumber.world.WorldClock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

public class ClockSteps {

    @Autowired
    private WorldClock worldClock;

    @Before
    public void setUp() {
        worldClock.reset();
    }

    @Given("current date time is {string}")
    public void given_current_date_time_is(final String dateTime) {
        worldClock.fixedAt(ZonedDateTime.parse(dateTime));
    }
}
