package io.fries.proxito.demo.cucumber.glue.world.request

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "demo.test.server")
data class ServerProperties(val port: Int)