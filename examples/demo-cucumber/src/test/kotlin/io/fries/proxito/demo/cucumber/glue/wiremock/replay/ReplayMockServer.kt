package io.fries.proxito.demo.cucumber.glue.wiremock.replay

import com.github.tomakehurst.wiremock.WireMockServer
import io.fries.proxito.demo.cucumber.glue.wiremock.MockServer
import io.fries.proxito.demo.cucumber.glue.world.World

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