package io.fries.demo.test.cucumber.wiremock.record.transformer

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer
import com.github.tomakehurst.wiremock.matching.RequestPattern
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.fries.demo.test.cucumber.wiremock.WireMockTransformerProperties

class RequestPatternTransformer(private val properties: WireMockTransformerProperties) : StubMappingTransformer() {

    override fun transform(stubMapping: StubMapping, files: FileSource, parameters: Parameters?) = stubMapping.apply {
        request = if (request.url != null) withTemplatedUrl(request) else request
    }

    private fun withTemplatedUrl(request: RequestPattern): RequestPattern = RequestPatternBuilder.newRequestPattern(
        request.method,
        WireMock.urlMatching(
            request.url
                .replace(properties.pattern.toString().toRegex(), properties.replacement)
                // `?` needs to be escaped in a regular expression.
                .replace("\\?".toRegex(), "\\\\?")
        )
    ).build()

    override fun getName(): String = "request-pattern-transformer"
}