package io.fries.demo.test.cucumber.wiremock.replay;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.fries.demo.test.cucumber.wiremock.MockServer;

public class ReplayMockServer implements MockServer {

    private final WireMockServer server;

    public ReplayMockServer(final WireMockServer server) {
        this.server = server;
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }
}
