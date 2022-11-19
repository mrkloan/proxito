package io.fries.api.test.proxy.wiremock

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.util.regex.Pattern

internal const val ROOT_DIRECTORY: String = "src/test/resources/wiremock"

@ConstructorBinding
@ConfigurationProperties(prefix = "test.wiremock")
data class WireMockProperties(
    val proxies: Map<String, ProxyServerProperties> = mapOf()
)

data class ProxyServerProperties(
    val port: Int,
    val endpoint: String?,
    val transformers: ProxyTransformersProperties?
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