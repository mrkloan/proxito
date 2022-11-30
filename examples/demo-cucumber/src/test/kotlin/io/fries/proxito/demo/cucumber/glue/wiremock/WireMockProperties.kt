package io.fries.proxito.demo.cucumber.glue.wiremock

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.regex.Pattern

@ConfigurationProperties(prefix = "demo.test.wiremock")
data class WireMockProperties(val servers: Map<String, WireMockServerProperties>)

data class WireMockServerProperties(
    val port: Int,
    val endpoint: String,
    val transformers: WireMockTransformersProperties?
) {
    init {
        require(port in 1..65535) { "port should be between 1 and 65535" }
        require(endpoint.isNotBlank()) { "endpoint cannot be blank" }
    }
}

data class WireMockTransformersProperties(
    val request: WireMockTransformerProperties?,
    val response: WireMockTransformerProperties?
)

data class WireMockTransformerProperties(
    val pattern: Pattern,
    val replacement: String
) {
    init {
        require(replacement.isNotBlank()) { "replacement cannot be blank" }
    }
}