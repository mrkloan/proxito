package io.fries.demo.api.journey

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("demo.navitia")
@ConstructorBinding
data class NavitiaProperties(
    val endpoint: String,
    val token: String
)