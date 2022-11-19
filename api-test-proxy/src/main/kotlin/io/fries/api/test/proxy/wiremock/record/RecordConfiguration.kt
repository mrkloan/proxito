package io.fries.api.test.proxy.wiremock.record

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.Extension
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.api.test.ApiTestContext
import io.fries.api.test.ApiTestProperties
import io.fries.api.test.ProxyServerProperties
import io.fries.api.test.proxy.ProxyServer
import io.fries.api.test.proxy.ProxyServers
import io.fries.api.test.proxy.ProxyServersFactory
import io.fries.api.test.proxy.wiremock.record.transformer.ConnectionCloseResponseTransformer
import io.fries.api.test.proxy.wiremock.record.transformer.IdempotentStubIdTransformer
import io.fries.api.test.proxy.wiremock.record.transformer.RequestPatternTransformer
import io.fries.api.test.proxy.wiremock.record.transformer.ResponseDateTransformer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.util.FileSystemUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime

@Configuration
@Profile("record")
class RecordConfiguration {

    @Bean
    fun proxyServersFactory(apiTestProperties: ApiTestProperties, clock: () -> ZonedDateTime): ProxyServersFactory =
        ProxyServersFactory { apiTestContext ->
            ProxyServers(toRecordMockServers(apiTestContext, apiTestProperties, clock))
        }

    private fun toRecordMockServers(
        apiTestContext: ApiTestContext,
        apiTestProperties: ApiTestProperties,
        clock: () -> ZonedDateTime
    ): List<ProxyServer> {
        return apiTestProperties.proxies.entries
            .map { properties ->
                RecordProxyServer(
                    toWireMockServer(apiTestContext, properties, clock),
                    toRecordSpec(properties.value)
                )
            }
    }

    private fun toWireMockServer(
        apiTestContext: ApiTestContext,
        properties: Map.Entry<String, ProxyServerProperties>,
        clock: () -> ZonedDateTime
    ): WireMockServer {
        val serverName = properties.key
        val serverProperties = properties.value
        val rootDirectory = Path.of("$apiTestContext/$serverName")
        createStubsDirectories(rootDirectory)

        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory(rootDirectory.toString())
                .extensions(*toExtensions(properties, clock))
        )
    }

    private fun toExtensions(
        properties: Map.Entry<String, ProxyServerProperties>,
        clock: () -> ZonedDateTime
    ): Array<Extension> = listOfNotNull(
        IdempotentStubIdTransformer(properties.key),
        ConnectionCloseResponseTransformer(),
        requestPatternTransformer(properties.value),
        responseDateTransformer(properties.value, clock)
    ).toTypedArray()

    private fun responseDateTransformer(serverProperties: ProxyServerProperties, clock: () -> ZonedDateTime) =
        serverProperties.transformers
            ?.response
            ?.let { ResponseDateTransformer(clock, it) }

    private fun requestPatternTransformer(serverProperties: ProxyServerProperties) =
        serverProperties.transformers
            ?.request
            ?.let { RequestPatternTransformer(it) }

    private fun createStubsDirectories(rootDirectory: Path) {
        try {
            FileSystemUtils.deleteRecursively(rootDirectory)
            Files.createDirectories(rootDirectory.resolve("__files"))
            Files.createDirectories(rootDirectory.resolve("mappings"))
        } catch (e: IOException) {
            throw IllegalStateException("Unable to create stubs directory under root: $rootDirectory", e)
        }
    }

    private fun toRecordSpec(properties: ProxyServerProperties): RecordSpec = WireMock.recordSpec()
        .forTarget(properties.endpoint)
        .ignoreRepeatRequests()
        .makeStubsPersistent(true)
        .extractTextBodiesOver(0)
        .build()
}