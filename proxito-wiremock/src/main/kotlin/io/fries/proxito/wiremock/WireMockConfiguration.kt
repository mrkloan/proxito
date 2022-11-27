package io.fries.proxito.wiremock

import io.fries.proxito.core.proxy.ProxyServers
import io.fries.proxito.core.proxy.ProxyServersFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class WireMockConfiguration {

    @Bean
    @Primary
    fun proxyServerFactory(factories: List<ProxyServersFactory>): ProxyServersFactory =
        ProxyServersFactory { context ->
            factories.map { it.create(context) }.reduce(ProxyServers::plus)
        }
}