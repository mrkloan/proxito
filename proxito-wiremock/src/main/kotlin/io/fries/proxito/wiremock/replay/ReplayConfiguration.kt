package io.fries.proxito.wiremock.replay

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import io.fries.proxito.core.context.ProxitoContext
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
    ): ProxyServersFactory = ProxyServersFactory { context ->
        ProxyServers(
            toReplayProxyServers(
                context,
                wireMockProperties,
                responseTemplateTransformer
            )
        )
    }

    private fun toReplayProxyServers(
        context: ProxitoContext,
        wireMockProperties: WireMockProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): List<ProxyServer> {
        return wireMockProperties.replay
            .map { serverProperties -> toWireMockServer(context, serverProperties, responseTemplateTransformer) }
            .map { server -> ReplayProxyServer(server) }
    }

    private fun toWireMockServer(
        context: ProxitoContext,
        serverProperties: ProxyServerProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): WireMockServer {
        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory("$ROOT_DIRECTORY/${context.path()}/${serverProperties.name}")
                .extensions(responseTemplateTransformer)
        )
    }

    @Bean
    fun responseTemplateTransformer(dateTemplate: DateTemplate): ResponseTemplateTransformer =
        ResponseTemplateTransformer.builder()
            .helper(DateTemplate.NAME, dateTemplate)
            .build()
}