package io.fries.demo.test.cucumber.wiremock.replay;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.fries.demo.test.cucumber.wiremock.MockServer;
import io.fries.demo.test.cucumber.world.World;

public class ReplayMockServer implements MockServer {

    private final World world;
    private final WireMockServer server;

    public ReplayMockServer(final World world, final WireMockServer server) {
        this.world = world;
        this.server = server;
    }

    @Override
    public void start() {
        server.start();
        server.addMockServiceRequestListener(world.tracker()::track);
    }

    @Override
    public void stop() {
        server.stop();
    }
}
