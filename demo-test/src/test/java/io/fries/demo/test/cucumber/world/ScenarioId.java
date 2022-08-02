package io.fries.demo.test.cucumber.world;

import io.cucumber.java.Scenario;

import static java.util.Objects.requireNonNull;

public record ScenarioId(String value) {

    private static final String FEATURES_BASE_DIRECTORY = "features/";

    public ScenarioId {
        requireNonNull(value, "value cannot be null");
    }

    public static ScenarioId from(final Scenario scenario) {
        final var scenarioUri = scenario.getUri().toString();
        final var scenarioDirectories = scenarioUri.substring(
                scenarioUri.indexOf(FEATURES_BASE_DIRECTORY) + FEATURES_BASE_DIRECTORY.length(),
                scenarioUri.lastIndexOf('.')
        );
        final var scenarioName = scenario.getName().toLowerCase().replaceAll(" ", "-");
        final var scenarioPath = String.format("%s/%s", scenarioDirectories, scenarioName);

        return new ScenarioId(scenarioPath);
    }

    @Override
    public String toString() {
        return value;
    }
}
