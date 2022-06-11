package io.fries.demo.test.cucumber.wiremock.record;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.recording.RecordSpec;
import io.cucumber.java.Scenario;
import io.fries.demo.test.cucumber.wiremock.MockServer;
import io.fries.demo.test.cucumber.wiremock.MockServers;
import io.fries.demo.test.cucumber.wiremock.MockServersFactory;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockServerProperties;
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
import static io.fries.demo.test.cucumber.ScenarioExtensions.normalizedName;
import static java.lang.String.format;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

@Configuration
@Profile("record")
public class RecordConfiguration {

    @Bean
    public MockServersFactory mockServersFactory(final WireMockProperties properties) {
        return scenario -> new MockServers(toRecordMockServers(scenario, properties));
    }

    private List<MockServer> toRecordMockServers(final Scenario scenario, final WireMockProperties properties) {
        return properties.servers().stream()
                .map(serverProperties -> new RecordMockServer(
                        toWireMockServer(scenario, serverProperties),
                        toRecordSpec(serverProperties)
                ))
                .collect(toList());
    }

    private WireMockServer toWireMockServer(final Scenario scenario, final WireMockServerProperties properties) {
        final var rootDirectory = format("src/test/resources/wiremock/%s/%s", normalizedName(scenario), properties.partner());
        createStubsDirectories(rootDirectory);

        return new WireMockServer(wireMockConfig()
                .port(properties.port())
                .withRootDirectory(rootDirectory)
//                .extensions(new RecordStubTransformer(properties.pattern(), properties.replacement()))
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void resetDirectory(final Path directory) {
        if (Files.notExists(directory)) {
            return;
        }

        try (final var files = Files.walk(directory)) {
            files.sorted(reverseOrder())
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
