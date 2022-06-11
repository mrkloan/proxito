package io.fries.demo.test.cucumber.wiremock.replay;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.fries.demo.test.cucumber.wiremock.MockServer;
import io.fries.demo.test.cucumber.wiremock.MockServers;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Configuration
@Profile("replay")
public class ReplayConfiguration {

    @Bean
    public MockServers mockServers(
            final WireMockProperties properties,
            final ResponseTemplateTransformer responseTemplateTransformer
    ) {
        return new MockServers(toReplayMockServers(properties, responseTemplateTransformer));
    }

    private List<MockServer> toReplayMockServers(final WireMockProperties properties, final ResponseTemplateTransformer responseTemplateTransformer) {
        return properties.servers().stream()
                .map(serverProperties -> toWireMockServer(serverProperties, responseTemplateTransformer))
                .map(ReplayMockServer::new)
                .collect(toList());
    }

    private WireMockServer toWireMockServer(final WireMockServerProperties properties, final ResponseTemplateTransformer responseTemplateTransformer) {
        return new WireMockServer(wireMockConfig()
                .port(properties.port())
                .fileSource(new ClasspathFileSource(format("wiremock/%s", properties.partner())))
                .extensions(responseTemplateTransformer)
        );
    }
}
