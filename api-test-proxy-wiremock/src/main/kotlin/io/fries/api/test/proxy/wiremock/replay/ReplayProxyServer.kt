package io.fries.api.test.proxy.wiremock.replay

import com.github.tomakehurst.wiremock.WireMockServer
import io.fries.api.test.proxy.ProxyServer
import org.assertj.core.api.Assertions.assertThat

class ReplayProxyServer(private val server: WireMockServer) : ProxyServer {

    override fun start() = server.start()

    override fun stop() {
        server.stop()

        assertThat(server.findAllUnmatchedRequests())
            .describedAs("Some unrecorded requests were performed")
            .isEmpty()
    }
}