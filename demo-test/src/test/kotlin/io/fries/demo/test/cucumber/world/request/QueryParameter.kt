package io.fries.demo.test.cucumber.world.request

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

data class QueryParameter(
    val name: String,
    private val rawValue: String
) {
    val encodedValue: String = URLEncoder.encode(rawValue, UTF_8)

    init {
        require(name.isNotBlank()) { "name cannot be blank" }
        require(encodedValue.isNotBlank()) { "value cannot be blank" }
    }
}