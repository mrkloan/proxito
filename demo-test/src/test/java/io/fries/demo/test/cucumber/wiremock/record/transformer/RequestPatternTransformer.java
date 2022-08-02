package io.fries.demo.test.cucumber.wiremock.record.transformer;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockTransformerProperties;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

public class RequestPatternTransformer extends StubMappingTransformer {

    private final WireMockTransformerProperties properties;

    public RequestPatternTransformer(final WireMockTransformerProperties properties) {
        this.properties = properties;
    }

    @Override
    public StubMapping transform(final StubMapping stubMapping, final FileSource files, final Parameters parameters) {
        final var request = stubMapping.getRequest();
        final var updatedRequest = (request.getUrl() != null)
                ? withTemplatedUrl(request)
                : request;

        stubMapping.setRequest(updatedRequest);
        return stubMapping;
    }

    private RequestPattern withTemplatedUrl(final RequestPattern request) {
        return RequestPatternBuilder.newRequestPattern(
                request.getMethod(),
                urlMatching(request.getUrl()
                        .replaceAll(properties.pattern().toString(), properties.replacement())
                        // `?` needs to be escaped in a regular expression.
                        .replaceAll("\\?", "\\\\?")
                )
        ).build();
    }

    @Override
    public String getName() {
        return "request-pattern-transformer";
    }
}
