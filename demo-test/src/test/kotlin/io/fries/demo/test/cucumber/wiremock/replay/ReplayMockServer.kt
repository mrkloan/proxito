package io.fries.demo.test.cucumber.wiremock.replay

import com.github.tomakehurst.wiremock.WireMockServer
import io.fries.demo.test.cucumber.wiremock.MockServer
import io.fries.demo.test.cucumber.world.World

class ReplayMockServer(
    private val world: World,
    private val server: WireMockServer
) : MockServer {

    override fun start() = with(server) {
        start()
        addMockServiceRequestListener { request, response ->
            world.tracker().track(request, response)
        }
    }

    override fun stop() = server.stop()
}