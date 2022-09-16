package io.fries.demo.test.cucumber.world.scenario

import io.cucumber.java.Scenario
import java.text.Normalizer
import java.util.*

class ScenarioId(private val value: String) {

    companion object {

        private const val FEATURES_BASE_DIRECTORY = "features/"

        fun from(scenario: Scenario): ScenarioId {
            val scenarioUri = scenario.uri.toString()
            val scenarioDirectories = scenarioUri.substring(
                scenarioUri.indexOf(FEATURES_BASE_DIRECTORY) + FEATURES_BASE_DIRECTORY.length,
                scenarioUri.lastIndexOf('.')
            )
            val scenarioName = Normalizer
                .normalize(scenario.name, Normalizer.Form.NFD)
                .lowercase(Locale.getDefault())
                .replace(" ".toRegex(), "-")
                .replace("[^a-z\\d\\-]".toRegex(), "")

            return ScenarioId("$scenarioDirectories/$scenarioName")
        }
    }

    override fun toString(): String = value
}