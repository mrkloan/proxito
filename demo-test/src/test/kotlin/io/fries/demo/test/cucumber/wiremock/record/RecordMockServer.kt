package io.fries.demo.test.cucumber.wiremock.record

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.demo.test.cucumber.wiremock.MockServer

class RecordMockServer(
    private val server: WireMockServer,
    private val recordSpec: RecordSpec
) : MockServer {

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