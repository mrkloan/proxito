package io.fries.proxito.demo.junit.journey

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("demo.navitia")
data class NavitiaProperties(
    val endpoint: String,
    val token: String
)