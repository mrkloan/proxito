package io.fries.proxito.wiremock.record

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.Extension
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.proxito.core.ApiTestContext
import io.fries.proxito.core.proxy.ProxyServer
import io.fries.proxito.core.proxy.ProxyServers
import io.fries.proxito.core.proxy.ProxyServersFactory
import io.fries.proxito.wiremock.ProxyServerProperties
import io.fries.proxito.wiremock.ROOT_DIRECTORY
import io.fries.proxito.wiremock.WireMockProperties
import io.fries.proxito.wiremock.record.transformer.ConnectionCloseResponseTransformer
import io.fries.proxito.wiremock.record.transformer.IdempotentStubIdTransformer
import io.fries.proxito.wiremock.record.transformer.RequestPatternTransformer
import io.fries.proxito.wiremock.record.transformer.ResponseDateTransformer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.FileSystemUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime

@Configuration
class RecordConfiguration {

    @Bean
    fun proxyServersFactory(wireMockProperties: WireMockProperties, clock: () -> ZonedDateTime): ProxyServersFactory =
        ProxyServersFactory { apiTestContext ->
            ProxyServers(toRecordProxyServers(apiTestContext, wireMockProperties, clock))
        }

    private fun toRecordProxyServers(
        apiTestContext: ApiTestContext,
        wireMockProperties: WireMockProperties,
        clock: () -> ZonedDateTime
    ): List<ProxyServer> {
        return wireMockProperties.record
            .map { serverProperties ->
                RecordProxyServer(
                    toWireMockServer(apiTestContext, serverProperties, clock),
                    toRecordSpec(serverProperties)
                )
            }
    }

    private fun toWireMockServer(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties,
        clock: () -> ZonedDateTime
    ): WireMockServer {
        val rootDirectory = Path.of("$ROOT_DIRECTORY/$apiTestContext/${serverProperties.name}")
        createStubsDirectories(rootDirectory)

        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory(rootDirectory.toString())
                .extensions(*toExtensions(serverProperties, clock))
        )
    }

    private fun toExtensions(
        serverProperties: ProxyServerProperties,
        clock: () -> ZonedDateTime
    ): Array<Extension> = listOfNotNull(
        IdempotentStubIdTransformer(serverProperties.name),
        ConnectionCloseResponseTransformer(),
        requestPatternTransformer(serverProperties),
        responseDateTransformer(serverProperties, clock)
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

    private fun toRecordSpec(serverProperties: ProxyServerProperties): RecordSpec = WireMock.recordSpec()
        .forTarget(serverProperties.endpoint)
        .ignoreRepeatRequests()
        .makeStubsPersistent(true)
        .extractTextBodiesOver(0)
        .build()
}