package io.fries.demo.test.cucumber.wiremock;

import io.fries.demo.test.cucumber.world.World;

public interface MockServersFactory {
    MockServers createFor(final World world);
}
