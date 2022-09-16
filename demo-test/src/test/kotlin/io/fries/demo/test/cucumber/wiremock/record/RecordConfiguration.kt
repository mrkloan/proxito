package io.fries.demo.test.cucumber.wiremock.record

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.Extension
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.demo.test.cucumber.wiremock.*
import io.fries.demo.test.cucumber.wiremock.record.transformer.ConnectionCloseResponseTransformer
import io.fries.demo.test.cucumber.wiremock.record.transformer.IdempotentStubIdTransformer
import io.fries.demo.test.cucumber.wiremock.record.transformer.RequestPatternTransformer
import io.fries.demo.test.cucumber.wiremock.record.transformer.ResponseDateTransformer
import io.fries.demo.test.cucumber.world.World
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator.reverseOrder

@Configuration
@Profile("record")
class RecordConfiguration {

    @Bean
    fun mockServersFactory(wireMockProperties: WireMockProperties): MockServersFactory = { world ->
        MockServers(toRecordMockServers(world, wireMockProperties))
    }

    private fun toRecordMockServers(world: World, wireMockProperties: WireMockProperties): List<MockServer> {
        return wireMockProperties.servers.entries
            .map { properties ->
                RecordMockServer(
                    toWireMockServer(world, properties),
                    toRecordSpec(properties.value)
                )
            }
    }

    private fun toWireMockServer(
        world: World,
        properties: Map.Entry<String, WireMockServerProperties>
    ): WireMockServer {
        val serverName = properties.key
        val serverProperties = properties.value
        val rootDirectory = "src/test/resources/wiremock/${world.scenarioId()}/$serverName"
        createStubsDirectories(rootDirectory)

        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory(rootDirectory)
                .extensions(*toExtensions(world, properties))
        )
    }

    private fun toExtensions(
        world: World,
        properties: Map.Entry<String, WireMockServerProperties>
    ): Array<Extension> = listOfNotNull(
        IdempotentStubIdTransformer(properties.key),
        ConnectionCloseResponseTransformer(),
        requestPatternTransformer(properties.value),
        responseDateTransformer(properties.value, world)
    ).toTypedArray()

    private fun responseDateTransformer(serverProperties: WireMockServerProperties, world: World) =
        serverProperties.transformers
            ?.response
            ?.let { ResponseDateTransformer(world.clock(), it) }

    private fun requestPatternTransformer(serverProperties: WireMockServerProperties) =
        serverProperties.transformers
            ?.request
            ?.let { RequestPatternTransformer(it) }

    private fun createStubsDirectories(rootDirectory: String) {
        val filesDirectory = Path.of("$rootDirectory/__files")
        val mappingsDirectory = Path.of("$rootDirectory/mappings")

        try {
            resetDirectory(filesDirectory)
            Files.createDirectories(filesDirectory)
            resetDirectory(mappingsDirectory)
            Files.createDirectories(mappingsDirectory)
        } catch (e: IOException) {
            throw IllegalStateException("Unable to create stubs directory under root: $rootDirectory", e)
        }
    }

    private fun resetDirectory(directory: Path) {
        if (Files.notExists(directory)) {
            return
        }
        try {
            Files.walk(directory).use { files ->
                files.sorted(reverseOrder())
                    .map { it.toFile() }
                    .forEach { it.delete() }
            }
        } catch (e: IOException) {
            throw IllegalStateException("Unable to reset directory: $directory", e)
        }
    }

    private fun toRecordSpec(properties: WireMockServerProperties): RecordSpec = WireMock.recordSpec()
        .forTarget(properties.endpoint)
        .ignoreRepeatRequests()
        .makeStubsPersistent(true)
        .extractTextBodiesOver(0)
        .build()
}