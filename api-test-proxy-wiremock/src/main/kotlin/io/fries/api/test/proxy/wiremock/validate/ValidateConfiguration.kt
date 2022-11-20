package io.fries.api.test.proxy.wiremock.validate

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.Extension
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.api.test.ApiTestContext
import io.fries.api.test.proxy.ProxyServer
import io.fries.api.test.proxy.ProxyServers
import io.fries.api.test.proxy.ProxyServersFactory
import io.fries.api.test.proxy.wiremock.ProxyServerProperties
import io.fries.api.test.proxy.wiremock.ROOT_DIRECTORY
import io.fries.api.test.proxy.wiremock.WireMockProperties
import io.fries.api.test.proxy.wiremock.validate.validation.json.JsonResponseValidation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ValidateConfiguration {

    @Bean
    fun validateProxyServersFactory(wireMockProperties: WireMockProperties): ProxyServersFactory =
        ProxyServersFactory { apiTestContext ->
            ProxyServers(toValidateProxyServer(apiTestContext, wireMockProperties))
        }

    private fun toValidateProxyServer(
        apiTestContext: ApiTestContext,
        wireMockProperties: WireMockProperties
    ): List<ProxyServer> = wireMockProperties.validate.map { serverProperties ->
        ValidateProxyServer(
            toWireMockServer(apiTestContext, serverProperties),
            toRecordSpec(serverProperties)
        )
    }

    private fun toWireMockServer(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties
    ): WireMockServer {
        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory("$ROOT_DIRECTORY/$apiTestContext/${serverProperties.name}")
                .extensions(*toExtensions(apiTestContext, serverProperties))
        )
    }

    private fun toExtensions(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties
    ): Array<Extension> = listOfNotNull(
        jsonResponseValidation(apiTestContext, serverProperties)
    ).toTypedArray()

    private fun jsonResponseValidation(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties
    ): JsonResponseValidation? = serverProperties.validators
        ?.json
        ?.let { JsonResponseValidation(apiTestContext, serverProperties.name, it) }

    private fun toRecordSpec(properties: ProxyServerProperties): RecordSpec = WireMock.recordSpec()
        .forTarget(properties.endpoint)
        .ignoreRepeatRequests()
        .makeStubsPersistent(false)
        .extractTextBodiesOver(0)
        .build()
}