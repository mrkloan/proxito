package io.fries.wiremock.demo.wiremock.cucumber.wiremock;

import java.util.List;

public class MockServers implements MockServer {

    private final List<MockServer> servers;

    public MockServers(final List<MockServer> servers) {
        this.servers = servers;
    }

    @Override
    public void start() {
        servers.forEach(MockServer::start);
    }

    @Override
    public void stop() {
        servers.forEach(MockServer::stop);
    }
}
