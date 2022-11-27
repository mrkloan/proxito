package io.fries.proxito.wiremock.validate

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
import io.fries.proxito.wiremock.replay.template.DateTemplate
import io.fries.proxito.wiremock.validate.validation.json.JsonResponseValidation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ValidateConfiguration {

    @Bean
    fun validateProxyServersFactory(
        wireMockProperties: WireMockProperties,
        dateTemplate: DateTemplate
    ): ProxyServersFactory =
        ProxyServersFactory { apiTestContext ->
            ProxyServers(toValidateProxyServer(apiTestContext, wireMockProperties, dateTemplate))
        }

    private fun toValidateProxyServer(
        apiTestContext: ApiTestContext,
        wireMockProperties: WireMockProperties,
        dateTemplate: DateTemplate
    ): List<ProxyServer> = wireMockProperties.validate.map { serverProperties ->
        ValidateProxyServer(
            toWireMockServer(apiTestContext, serverProperties, dateTemplate),
            toRecordSpec(serverProperties)
        )
    }

    private fun toWireMockServer(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties,
        dateTemplate: DateTemplate
    ): WireMockServer {
        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory("$ROOT_DIRECTORY/$apiTestContext/${serverProperties.name}")
                .extensions(*toExtensions(apiTestContext, serverProperties, dateTemplate))
        )
    }

    private fun toExtensions(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties,
        dateTemplate: DateTemplate
    ): Array<Extension> = listOfNotNull(
        jsonResponseValidation(apiTestContext, serverProperties, dateTemplate)
    ).toTypedArray()

    private fun jsonResponseValidation(
        apiTestContext: ApiTestContext,
        serverProperties: ProxyServerProperties,
        dateTemplate: DateTemplate
    ): JsonResponseValidation? = serverProperties.validators
        ?.json
        ?.let { JsonResponseValidation(apiTestContext, serverProperties.name, it, dateTemplate) }

    private fun toRecordSpec(properties: ProxyServerProperties): RecordSpec = WireMock.recordSpec()
        .forTarget(properties.endpoint)
        .ignoreRepeatRequests()
        .makeStubsPersistent(false)
        .extractTextBodiesOver(0)
        .build()
}