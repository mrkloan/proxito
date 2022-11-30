package io.fries.proxito.demo.cucumber.glue.wiremock.record.transformer

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import wiremock.org.apache.hc.core5.http.HttpHeaders.CONNECTION

class ConnectionCloseResponseTransformer : ResponseDefinitionTransformer() {

    override fun transform(
        request: Request,
        responseDefinition: ResponseDefinition,
        files: FileSource,
        parameters: Parameters?
    ): ResponseDefinition = ResponseDefinitionBuilder.like(responseDefinition)
        .withHeader(CONNECTION, "close")
        .build()

    override fun getName(): String = "connection-close-response-transformer"
}