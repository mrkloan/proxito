package io.fries.demo.test.cucumber.wiremock.record;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.recording.RecordSpec;
import io.fries.demo.test.cucumber.wiremock.MockServer;
import io.fries.demo.test.cucumber.wiremock.MockServers;
import io.fries.demo.test.cucumber.wiremock.MockServersFactory;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockServerProperties;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockTransformersProperties;
import io.fries.demo.test.cucumber.wiremock.record.transformer.ConnectionCloseResponseTransformer;
import io.fries.demo.test.cucumber.wiremock.record.transformer.IdempotentStubIdTransformer;
import io.fries.demo.test.cucumber.wiremock.record.transformer.RequestPatternTransformer;
import io.fries.demo.test.cucumber.wiremock.record.transformer.ResponseDateTransformer;
import io.fries.demo.test.cucumber.world.World;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

@Configuration
@Profile("record")
public class RecordConfiguration {

    @Bean
    public MockServersFactory mockServersFactory(final WireMockProperties wireMockProperties) {
        return world -> new MockServers(toRecordMockServers(world, wireMockProperties));
    }

    private List<MockServer> toRecordMockServers(final World world, final WireMockProperties wireMockProperties) {
        return wireMockProperties.servers().entrySet().stream()
                .map(properties -> new RecordMockServer(
                        toWireMockServer(world, properties),
                        toRecordSpec(properties.getValue())
                ))
                .collect(toList());
    }

    private WireMockServer toWireMockServer(
            final World world,
            final Map.Entry<String, WireMockServerProperties> properties
    ) {
        final var serverName = properties.getKey();
        final var serverProperties = properties.getValue();

        final var rootDirectory = format("src/test/resources/wiremock/%s/%s", world.scenarioId(), serverName);
        createStubsDirectories(rootDirectory);

        return new WireMockServer(wireMockConfig()
                .port(serverProperties.port())
                .withRootDirectory(rootDirectory)
                .extensions(toExtensions(world, properties))
        );
    }

    private Extension[] toExtensions(
            final World world,
            final Map.Entry<String, WireMockServerProperties> properties
    ) {
        final var serverName = properties.getKey();
        final var serverProperties = properties.getValue();

        final var recordRequestTransformer = Optional
                .ofNullable(serverProperties.transformers())
                .map(WireMockTransformersProperties::request)
                .map(RequestPatternTransformer::new);
        final var recordResponseTransformer = Optional
                .ofNullable(serverProperties.transformers())
                .map(WireMockTransformersProperties::response)
                .map(transformerProperties -> new ResponseDateTransformer(world.clock(), transformerProperties));

        return Stream.of(
                        Optional.of(new IdempotentStubIdTransformer(serverName)),
                        Optional.of(new ConnectionCloseResponseTransformer()),
                        recordRequestTransformer,
                        recordResponseTransformer
                )
                .flatMap(Optional::stream)
                .toArray(Extension[]::new);
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
                .extractTextBodiesOver(0)
                .build();
    }
}
