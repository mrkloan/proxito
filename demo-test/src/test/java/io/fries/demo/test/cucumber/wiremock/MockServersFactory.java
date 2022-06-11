package io.fries.demo.test.cucumber.wiremock;

import io.cucumber.java.Scenario;

public interface MockServersFactory {
    MockServers createFor(final Scenario scenario);
}
