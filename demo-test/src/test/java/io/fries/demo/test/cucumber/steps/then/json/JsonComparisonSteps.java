package io.fries.demo.test.cucumber.steps.then.json;

import io.cucumber.java.en.Then;
import io.fries.demo.test.cucumber.world.World;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static io.fries.demo.test.cucumber.steps.then.json.JsonStringAssert.assertThatJsonString;
import static java.lang.String.format;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.fail;

public class JsonComparisonSteps {

    private static final Set<String> FIELDS_TO_IGNORE = Set.of("href");

    @Autowired
    private World world;
    @Autowired
    private Environment environment;

    @Then("the json response matches")
    public void then_the_json_response_matches() {
        if (isNotReplay()) {
            return;
        }

        final var responsesDirectory = Path.of(format("src/test/resources/wiremock/%s/api/__files", world.scenarioId()));
        final var expectedResponse = readExpectedResponseIn(responsesDirectory);
        final var actualResponse = world.response().getBody();

        assertThatJsonString(actualResponse).isEqualToIgnoringFields(expectedResponse, FIELDS_TO_IGNORE);
    }

    private boolean isNotReplay() {
        return !environment.acceptsProfiles(Profiles.of("replay"));
    }

    private String readExpectedResponseIn(final Path directory) {
        if (Files.notExists(directory)) {
            return fail(format("Directory %s does not exist", directory));
        }

        try (final var files = Files.walk(directory)) {
            return files
                    .filter(not(Files::isDirectory))
                    .findFirst()
                    .map(this::readFile)
                    .orElseGet(() -> fail(format("Directory %s is empty", directory)));
        } catch (final IOException e) {
            return fail(e.getMessage(), e);
        }
    }

    private String readFile(final Path path) {
        try {
            return Files.readString(path);
        } catch (final IOException e) {
            return fail(e.getMessage(), e);
        }
    }
}
