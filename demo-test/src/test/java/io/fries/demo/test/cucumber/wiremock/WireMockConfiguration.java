package io.fries.demo.test.cucumber.wiremock;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.fries.demo.test.cucumber.world.WorldClock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.format.DateTimeFormatter;

@Configuration
@Profile("mock")
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockConfiguration {

    @Bean
    public ResponseTemplateTransformer responseTemplateTransformer(final WorldClock worldClock) {
        return ResponseTemplateTransformer.builder()
                .helper("date-format", (dateFormat, options) -> worldClock.now().format(DateTimeFormatter.ofPattern(dateFormat.toString())))
                .build();
    }
}
