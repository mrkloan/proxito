package io.fries.demo.test.cucumber.wiremock

class MockServers(
    private val servers: List<MockServer> = listOf()
) : MockServer {

    override fun start() {
        servers.forEach(MockServer::start)
    }

    override fun stop() {
        servers.forEach(MockServer::stop)
    }
}