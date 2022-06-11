package io.fries.demo.test.cucumber.wiremock.replay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.cucumber.java.Scenario;
import io.fries.demo.test.cucumber.wiremock.MockServer;
import io.fries.demo.test.cucumber.wiremock.MockServers;
import io.fries.demo.test.cucumber.wiremock.MockServersFactory;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockServerProperties;
import io.fries.demo.test.cucumber.wiremock.replay.template.ReplayDateTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.fries.demo.test.cucumber.ScenarioExtensions.normalizedName;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Configuration
@Profile("replay")
public class ReplayConfiguration {

    @Bean
    public MockServersFactory mockServersFactory(
            final WireMockProperties properties,
            final ResponseTemplateTransformer responseTemplateTransformer
    ) {
        return scenario -> new MockServers(toReplayMockServers(scenario, properties, responseTemplateTransformer));
    }

    private List<MockServer> toReplayMockServers(final Scenario scenario, final WireMockProperties properties, final ResponseTemplateTransformer responseTemplateTransformer) {
        return properties.servers().stream()
                .map(serverProperties -> toWireMockServer(scenario, serverProperties, responseTemplateTransformer))
                .map(ReplayMockServer::new)
                .collect(toList());
    }

    private WireMockServer toWireMockServer(final Scenario scenario, final WireMockServerProperties properties, final ResponseTemplateTransformer responseTemplateTransformer) {
        return new WireMockServer(wireMockConfig()
                .port(properties.port())
                .fileSource(new ClasspathFileSource(format("wiremock/%s/%s", normalizedName(scenario), properties.partner())))
                .extensions(responseTemplateTransformer)
        );
    }

    @Bean
    public ResponseTemplateTransformer responseTemplateTransformer(final ReplayDateTemplate replayDateTemplate) {
        return ResponseTemplateTransformer.builder()
                .helper(ReplayDateTemplate.NAME, replayDateTemplate)
                .build();
    }
}
