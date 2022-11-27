package io.fries.api.test.proxy.wiremock

import io.fries.api.test.ApiTestContext
import io.fries.api.test.proxy.ProxyServers
import io.fries.api.test.proxy.ProxyServersFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class WireMockConfiguration {

    @Bean
    @Primary
    fun proxyServerFactory(factories: List<ProxyServersFactory>): ProxyServersFactory =
        ProxyServersFactory { apiTestContext: ApiTestContext ->
            factories.map { it.create(apiTestContext) }.reduce(ProxyServers::plus)
        }
}