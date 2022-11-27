package io.fries.proxito.wiremock.record

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.proxito.core.proxy.ProxyServer

class RecordProxyServer(
    private val server: WireMockServer,
    private val recordSpec: RecordSpec
) : ProxyServer {

    override fun start() = with(server) {
        start()
        startRecording(recordSpec)
    }

    override fun stop() = with(server) {
        saveMappings()
        stopRecording()
        stop()
    }
}