package io.fries.demo.test.cucumber.wiremock.record.transformer;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class IdempotentStubIdTransformer extends StubMappingTransformer {

    private final String serverName;
    private final AtomicInteger requestsCounter;
    private final Set<UUID> previouslyAppliedOn;

    public IdempotentStubIdTransformer(final String serverName) {
        this.serverName = serverName;
        this.requestsCounter = new AtomicInteger();
        this.previouslyAppliedOn = new HashSet<>();
    }

    @Override
    public StubMapping transform(final StubMapping stubMapping, final FileSource files, final Parameters parameters) {
        if (previouslyAppliedOn.contains(stubMapping.getId())) {
            return stubMapping;
        }

        final var idempotentId = generateIdempotentUUID();
        updateResponseBodyFileName(stubMapping, files, idempotentId);
        stubMapping.setId(idempotentId);
        stubMapping.setName(serverName);

        previouslyAppliedOn.add(idempotentId);
        return stubMapping;
    }

    private UUID generateIdempotentUUID() {
        final var seed = String.format("%s-%d", serverName, requestsCounter.incrementAndGet()).getBytes(UTF_8);
        return UUID.nameUUIDFromBytes(seed);
    }

    private void updateResponseBodyFileName(final StubMapping stubMapping, final FileSource files, final UUID idempotentId) {
        final var response = stubMapping.getResponse();
        final var bodyFileName = response.getBodyFileName();
        if (bodyFileName == null) {
            return;
        }

        final var idempotentFileName = bodyFileName
                .replace(stubMapping.getName(), serverName)
                .replace(stubMapping.getId().toString(), idempotentId.toString());
        final var responseBody = files.getTextFileNamed(bodyFileName).readContentsAsString();
        files.deleteFile(bodyFileName);
        files.writeTextFile(idempotentFileName, responseBody);

        final var updatedResponse = ResponseDefinitionBuilder.like(response)
                .withBodyFile(idempotentFileName)
                .build();
        stubMapping.setResponse(updatedResponse);
    }

    @Override
    public String getName() {
        return "idempotent-stub-id-transformer";
    }
}
