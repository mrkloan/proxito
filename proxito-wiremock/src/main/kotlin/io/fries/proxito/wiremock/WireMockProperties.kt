package io.fries.proxito.wiremock

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.regex.Pattern

internal const val ROOT_DIRECTORY: String = "src/test/resources/wiremock"

@ConfigurationProperties(prefix = "test.wiremock")
data class WireMockProperties(
    val record: List<ProxyServerProperties> = listOf(),
    val replay: List<ProxyServerProperties> = listOf(),
    val validate: List<ProxyServerProperties> = listOf()
)

data class ProxyServerProperties(
    val name: String,
    val port: Int,
    val endpoint: String?,
    val transformers: ProxyTransformersProperties?,
    val validators: ProxyValidatorsProperties?
) {
    init {
        require(port in 1..65535) { "port should be between 1 and 65535" }
        require(endpoint?.isNotBlank() ?: true) { "endpoint cannot be blank" }
    }
}

data class ProxyTransformersProperties(
    val request: ProxyTransformerProperties?,
    val response: ProxyTransformerProperties?
)

data class ProxyTransformerProperties(
    val pattern: Pattern,
    val replacement: String
) {
    init {
        require(replacement.isNotBlank()) { "replacement cannot be blank" }
    }
}

data class ProxyValidatorsProperties(
    val json: JsonValidationProperties?
)

data class JsonValidationProperties(
    val fieldsToIgnore: Set<String> = setOf()
)