package io.fries.proxito.wiremock.validate

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.Extension
import com.github.tomakehurst.wiremock.recording.RecordSpec
import io.fries.proxito.core.context.ProxitoContext
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
        ProxyServersFactory { context ->
            ProxyServers(toValidateProxyServer(context, wireMockProperties, dateTemplate))
        }

    private fun toValidateProxyServer(
        context: ProxitoContext,
        wireMockProperties: WireMockProperties,
        dateTemplate: DateTemplate
    ): List<ProxyServer> = wireMockProperties.validate.map { serverProperties ->
        ValidateProxyServer(
            toWireMockServer(context, serverProperties, dateTemplate),
            toRecordSpec(serverProperties)
        )
    }

    private fun toWireMockServer(
        context: ProxitoContext,
        serverProperties: ProxyServerProperties,
        dateTemplate: DateTemplate
    ): WireMockServer {
        return WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(serverProperties.port)
                .withRootDirectory("$ROOT_DIRECTORY/${context.path()}/${serverProperties.name}")
                .extensions(*toExtensions(context, serverProperties, dateTemplate))
        )
    }

    private fun toExtensions(
        context: ProxitoContext,
        serverProperties: ProxyServerProperties,
        dateTemplate: DateTemplate
    ): Array<Extension> = listOfNotNull(
        jsonResponseValidation(context, serverProperties, dateTemplate)
    ).toTypedArray()

    private fun jsonResponseValidation(
        context: ProxitoContext,
        serverProperties: ProxyServerProperties,
        dateTemplate: DateTemplate
    ): JsonResponseValidation? = serverProperties.validators
        ?.json
        ?.let { JsonResponseValidation(context, serverProperties.name, it, dateTemplate) }

    private fun toRecordSpec(properties: ProxyServerProperties): RecordSpec = WireMock.recordSpec()
        .forTarget(properties.endpoint)
        .ignoreRepeatRequests()
        .makeStubsPersistent(false)
        .extractTextBodiesOver(0)
        .build()
}