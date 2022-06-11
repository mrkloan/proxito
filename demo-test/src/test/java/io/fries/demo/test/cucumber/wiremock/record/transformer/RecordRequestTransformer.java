package io.fries.demo.test.cucumber.wiremock.record.transformer;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockTransformerProperties;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

public class RecordRequestTransformer extends StubMappingTransformer {

    private final WireMockTransformerProperties properties;

    public RecordRequestTransformer(final WireMockTransformerProperties properties) {
        this.properties = properties;
    }

    @Override
    public StubMapping transform(final StubMapping stubMapping, final FileSource fileSource, final Parameters parameters) {
        final var request = stubMapping.getRequest();
        final var updatedRequest = RequestPatternBuilder.newRequestPattern(
                        request.getMethod(),
                        urlMatching(request.getUrl()
                                .replaceAll(properties.pattern().toString(), properties.replacement())
                                .replaceAll("\\?", "\\\\?")
                        )
                )
                .build();
        stubMapping.setRequest(updatedRequest);

        return stubMapping;
    }

    @Override
    public String getName() {
        return "record-request-transformer";
    }
}
