package io.fries.wiremock.demo.wiremock.cucumber.wiremock.replay;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.fries.wiremock.demo.wiremock.cucumber.wiremock.MockServer;

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
