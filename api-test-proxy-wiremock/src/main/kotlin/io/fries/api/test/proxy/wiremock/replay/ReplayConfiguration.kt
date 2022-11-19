package io.fries.api.test.proxy.wiremock.replay

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import io.fries.api.test.ApiTestContext
import io.fries.api.test.proxy.ProxyServer
import io.fries.api.test.proxy.ProxyServers
import io.fries.api.test.proxy.ProxyServersFactory
import io.fries.api.test.proxy.wiremock.ProxyServerProperties
import io.fries.api.test.proxy.wiremock.ROOT_DIRECTORY
import io.fries.api.test.proxy.wiremock.WireMockProperties
import io.fries.api.test.proxy.wiremock.replay.template.DateTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("replay")
class ReplayConfiguration {

    @Bean
    fun proxyServersFactory(
        wireMockProperties: WireMockProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): ProxyServersFactory = ProxyServersFactory { apiTestContext ->
        ProxyServers(
            toReplayMockServers(
                apiTestContext,
                wireMockProperties,
                responseTemplateTransformer
            )
        )
    }

    private fun toReplayMockServers(
        apiTestContext: ApiTestContext,
        wireMockProperties: WireMockProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): List<ProxyServer> {
        return wireMockProperties.proxies.entries
            .map { properties -> toWireMockServer(apiTestContext, properties, responseTemplateTransformer) }
            .map { server -> ReplayProxyServer(server) }
    }

    private fun toWireMockServer(
        apiTestContext: ApiTestContext,
        properties: Map.Entry<String, ProxyServerProperties>,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): WireMockServer {
        val serverName = properties.key
        val serverProperties: ProxyServerProperties = properties.value
        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory("$ROOT_DIRECTORY/$apiTestContext/$serverName")
                .extensions(responseTemplateTransformer)
        )
    }

    @Bean
    fun responseTemplateTransformer(dateTemplate: DateTemplate): ResponseTemplateTransformer =
        ResponseTemplateTransformer.builder()
            .helper(DateTemplate.NAME, dateTemplate)
            .build()
}