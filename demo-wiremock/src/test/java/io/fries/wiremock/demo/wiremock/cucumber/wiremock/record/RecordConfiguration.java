package io.fries.wiremock.demo.wiremock.cucumber.wiremock.record;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.recording.RecordSpec;
import io.fries.wiremock.demo.wiremock.cucumber.wiremock.MockServer;
import io.fries.wiremock.demo.wiremock.cucumber.wiremock.MockServers;
import io.fries.wiremock.demo.wiremock.cucumber.wiremock.WireMockProperties;
import io.fries.wiremock.demo.wiremock.cucumber.wiremock.WireMockProperties.WireMockServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

@Configuration
@Profile("record")
public class RecordConfiguration {

    @Bean
    public MockServers mockServers(
            final WireMockProperties properties,
            final ResponseTemplateTransformer responseTemplateTransformer
    ) {
        return new MockServers(toRecordMockServers(properties, responseTemplateTransformer));
    }

    private List<MockServer> toRecordMockServers(final WireMockProperties properties, final ResponseTemplateTransformer responseTemplateTransformer) {
        return properties.servers().stream()
                .map(serverProperties -> new RecordMockServer(
                        toWireMockServer(serverProperties, responseTemplateTransformer),
                        toRecordSpec(serverProperties)
                ))
                .collect(toList());
    }

    private WireMockServer toWireMockServer(final WireMockServerProperties properties, final ResponseTemplateTransformer responseTemplateTransformer) {
        final var rootDirectory = format("src/test/resources/wiremock/%s", properties.partner());
        createStubsDirectories(rootDirectory);

        return new WireMockServer(wireMockConfig()
                .port(properties.port())
                .withRootDirectory(rootDirectory)
                .extensions(
                        new RecordBodyTransformer(properties.pattern(), properties.replacement()),
                        responseTemplateTransformer
                )
        );
    }

    private void createStubsDirectories(final String rootDirectory) {
        final var filesDirectory = Path.of(format("%s/__files", rootDirectory));
        final var mappingsDirectory = Path.of(format("%s/mappings", rootDirectory));

        try {
            resetDirectory(filesDirectory);
            Files.createDirectories(filesDirectory);

            resetDirectory(mappingsDirectory);
            Files.createDirectories(mappingsDirectory);
        } catch (final IOException e) {
            throw new IllegalStateException(format("Unable to create stubs directory under root: %s", rootDirectory), e);
        }
    }

    private void resetDirectory(final Path directory) {
        if (Files.notExists(directory)) {
            return;
        }

        try {
            Files.walk(directory)
                    .sorted(reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new IllegalStateException(format("Unable to reset directory: %s", directory), e);
        }
    }

    private RecordSpec toRecordSpec(final WireMockServerProperties properties) {
        return recordSpec()
                .forTarget(properties.endpoint())
                .ignoreRepeatRequests()
                .makeStubsPersistent(true)
                .build();
    }
}
