package io.fries.demo.test.cucumber.wiremock.replay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.fries.demo.test.cucumber.wiremock.MockServer;
import io.fries.demo.test.cucumber.wiremock.MockServers;
import io.fries.demo.test.cucumber.wiremock.MockServersFactory;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockServerProperties;
import io.fries.demo.test.cucumber.wiremock.replay.template.DateTemplate;
import io.fries.demo.test.cucumber.world.World;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Configuration
@Profile("replay")
public class ReplayConfiguration {

    @Bean
    public MockServersFactory mockServersFactory(
            final WireMockProperties wireMockProperties,
            final ResponseTemplateTransformer responseTemplateTransformer
    ) {
        return world -> new MockServers(toReplayMockServers(world, wireMockProperties, responseTemplateTransformer));
    }

    private List<MockServer> toReplayMockServers(
            final World world,
            final WireMockProperties wireMockProperties,
            final ResponseTemplateTransformer responseTemplateTransformer
    ) {
        return wireMockProperties.servers().entrySet().stream()
                .map(properties -> toWireMockServer(world, properties, responseTemplateTransformer))
                .map(server -> new ReplayMockServer(world, server))
                .collect(toList());
    }

    private WireMockServer toWireMockServer(
            final World world,
            final Map.Entry<String, WireMockServerProperties> properties,
            final ResponseTemplateTransformer responseTemplateTransformer
    ) {
        final var serverName = properties.getKey();
        final var serverProperties = properties.getValue();

        return new WireMockServer(wireMockConfig()
                .port(serverProperties.port())
                .usingFilesUnderClasspath(format("wiremock/%s/%s", world.scenarioId(), serverName))
                .extensions(responseTemplateTransformer)
        );
    }

    @Bean
    public ResponseTemplateTransformer responseTemplateTransformer(final DateTemplate dateTemplate) {
        return ResponseTemplateTransformer.builder()
                .helper(DateTemplate.NAME, dateTemplate)
                .build();
    }
}
