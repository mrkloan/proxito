package io.fries.api.test.proxy.wiremock.validate

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.api.test.proxy.ProxyServer

class ValidateProxyServer(
    private val server: WireMockServer,
    private val recordSpec: RecordSpec
) : ProxyServer {

    override fun start() = with(server) {
        start()
        startRecording(recordSpec)
    }

    override fun stop() = with(server) {
        snapshotRecord(recordSpec)
        stop()
    }
}