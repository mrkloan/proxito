package io.fries.demo.test.cucumber.wiremock;

import java.util.List;

public class MockServers implements MockServer {

    private final List<MockServer> servers;

    public MockServers(final List<MockServer> servers) {
        this.servers = servers;
    }

    public static MockServers none() {
        return new MockServers(List.of());
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
