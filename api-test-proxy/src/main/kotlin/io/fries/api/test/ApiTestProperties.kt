package io.fries.api.test

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.test.context.TestPropertySource
import java.util.regex.Pattern

@TestPropertySource
@ConstructorBinding
@ConfigurationProperties(prefix = "api.test")
data class ApiTestProperties(
    val recordPath: String = "src/test/resources/records",
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