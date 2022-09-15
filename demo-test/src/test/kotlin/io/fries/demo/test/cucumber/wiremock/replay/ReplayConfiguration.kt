package io.fries.demo.test.cucumber.wiremock.replay

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import io.fries.demo.test.cucumber.wiremock.*
import io.fries.demo.test.cucumber.wiremock.replay.template.DateTemplate
import io.fries.demo.test.cucumber.world.World
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("replay")
class ReplayConfiguration {

    @Bean
    fun mockServersFactory(
        wireMockProperties: WireMockProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): MockServersFactory = { world ->
        MockServers(
            toReplayMockServers(
                world,
                wireMockProperties,
                responseTemplateTransformer
            )
        )
    }

    private fun toReplayMockServers(
        world: World,
        wireMockProperties: WireMockProperties,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): List<MockServer> {
        return wireMockProperties.servers.entries
            .map { properties -> toWireMockServer(world, properties, responseTemplateTransformer) }
            .map { server -> ReplayMockServer(world, server) }
    }

    private fun toWireMockServer(
        world: World,
        properties: Map.Entry<String, WireMockServerProperties>,
        responseTemplateTransformer: ResponseTemplateTransformer
    ): WireMockServer {
        val serverName = properties.key
        val serverProperties: WireMockServerProperties = properties.value
        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .usingFilesUnderClasspath("wiremock/${world.scenarioId()}/$serverName")
                .extensions(responseTemplateTransformer)
        )
    }

    @Bean
    fun responseTemplateTransformer(dateTemplate: DateTemplate): ResponseTemplateTransformer =
        ResponseTemplateTransformer.builder()
            .helper(DateTemplate.NAME, dateTemplate)
            .build()
}