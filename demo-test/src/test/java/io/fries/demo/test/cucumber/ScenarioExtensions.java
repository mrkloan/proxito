package io.fries.demo.test.cucumber;

import io.cucumber.java.Scenario;

public final class ScenarioExtensions {

    private ScenarioExtensions() {
    }

    public static String normalizedName(final Scenario scenario) {
        return scenario.getName().toLowerCase().replaceAll(" ", "-");
    }
}
