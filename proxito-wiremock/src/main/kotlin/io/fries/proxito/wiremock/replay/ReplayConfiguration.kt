package io.fries.proxito.wiremock.replay

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import io.fries.proxito.core.ApiTestContext
import io.fries.proxito.core.proxy.ProxyServer
import io.fries.proxito.core.proxy.ProxyServers
import io.fries.proxito.core.proxy.ProxyServersFactory
import io.fries.proxito.wiremock.ProxyServerProperties
import io.fries.proxito.wiremock.ROOT_DIRECTORY
import io.fries.proxito.wiremock.WireMockProperties
import io.fries.proxito.wiremock.replay.template.DateTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReplayConfiguration {

    @Bean
    fun replayProxyServersFactory(
        wireMockProperties: WireMockProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): ProxyServersFactory = ProxyServersFactory { apiTestContext ->
        ProxyServers(
            toReplayProxyServers(
                apiTestContext,
                wireMockProperties,
                responseTemplateTransformer
            )
        )
    }

    private fun toReplayProxyServers(
        apiTestContext: ApiTestContext,
        wireMockProperties: WireMockProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): List<ProxyServer> {
        return wireMockProperties.replay
            .map { serverProperties -> toWireMockServer(apiTestContext, serverProperties, responseTemplateTransformer) }
            .map { server -> ReplayProxyServer(server) }
    }

    private fun toWireMockServer(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): WireMockServer {
        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory("$ROOT_DIRECTORY/$apiTestContext/${serverProperties.name}")
                .extensions(responseTemplateTransformer)
        )
    }

    @Bean
    fun responseTemplateTransformer(dateTemplate: DateTemplate): ResponseTemplateTransformer =
        ResponseTemplateTransformer.builder()
            .helper(DateTemplate.NAME, dateTemplate)
            .build()
}