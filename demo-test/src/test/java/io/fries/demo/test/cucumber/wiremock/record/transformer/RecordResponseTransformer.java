package io.fries.demo.test.cucumber.wiremock.record.transformer;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.TextFile;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockTransformerProperties;
import io.fries.demo.test.cucumber.wiremock.replay.template.ReplayDateTemplate;

import java.util.Optional;

import static java.lang.String.format;

public class RecordResponseTransformer extends StubMappingTransformer {

    private final WireMockTransformerProperties properties;

    public RecordResponseTransformer(final WireMockTransformerProperties properties) {
        this.properties = properties;
    }

    @Override
    public StubMapping transform(final StubMapping stubMapping, final FileSource fileSource, final Parameters parameters) {
        final var response = stubMapping.getResponse();

        final var body = Optional.ofNullable(response.getBodyFileName())
                .map(fileSource::getTextFileNamed)
                .map(TextFile::readContentsAsString)
                .orElseGet(response::getBody);
        final var bodyTemplate = toBodyTemplate(body);

        final var bodyFileName = Optional.ofNullable(response.getBodyFileName())
                .orElseGet(() -> format("%s_%s.%s", stubMapping.getName(), stubMapping.getId(), toFileExtension(body)));
        final var updatedResponse = updateResponse(response, bodyFileName);

        fileSource.writeTextFile(bodyFileName, bodyTemplate);
        stubMapping.setResponse(updatedResponse);

        return stubMapping;
    }

    public String toBodyTemplate(final String body) {
        return properties.pattern()
                .matcher(body)
                .replaceAll(match -> toDateTemplate(match.group()));
    }

    private String toDateTemplate(final String date) {
        // TODO: compute offset duration.
        return format("{{{%s pattern='%s'}}}", ReplayDateTemplate.NAME, properties.replacement());
    }

    private String toFileExtension(final String body) {
        return body.startsWith("<?xml") ? "xml" : "json";
    }

    private ResponseDefinition updateResponse(final ResponseDefinition response, final String bodyFileName) {
        return ResponseDefinitionBuilder.like(response).but()
                .withBody((String) null)
                .withJsonBody(null)
                .withBase64Body(null)
                .withBodyFile(bodyFileName)
                .build();
    }

    @Override
    public String getName() {
        return "record-response-transformer";
    }
}
