package io.fries.demo.test.cucumber.wiremock.record;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.recording.RecordSpec;
import io.fries.demo.test.cucumber.wiremock.MockServer;

public class RecordMockServer implements MockServer {

    private final WireMockServer server;
    private final RecordSpec recordSpec;

    public RecordMockServer(final WireMockServer server, final RecordSpec recordSpec) {
        this.server = server;
        this.recordSpec = recordSpec;
    }

    @Override
    public void start() {
        server.start();
        server.startRecording(recordSpec);
    }

    @Override
    public void stop() {
        server.saveMappings();
        server.stopRecording();
        server.stop();
    }
}
