package io.fries.demo.test.cucumber.steps.then.json

import io.cucumber.java.en.Then
import io.fries.demo.test.cucumber.steps.then.json.JsonStringAssert.Companion.assertThatJsonString
import io.fries.demo.test.cucumber.world.World
import org.assertj.core.api.Assertions.fail
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class JsonComparisonSteps(
    private val world: World,
    private val environment: Environment
) {

    companion object {
        private val FIELDS_TO_IGNORE = setOf("href")
    }

    @Then("the json response matches")
    fun then_the_json_response_matches() {
        if (isNotReplay()) {
            return
        }

        val responsesDirectory = Path.of("src/test/resources/wiremock/${world.scenarioId()}/api/__files")
        val expectedResponse = readExpectedResponseIn(responsesDirectory)
        val actualResponse = world.response().body

        assertThatJsonString(actualResponse).isEqualToIgnoringFields(expectedResponse, FIELDS_TO_IGNORE)
    }

    private fun isNotReplay(): Boolean = !environment.acceptsProfiles(Profiles.of("replay"))

    private fun readExpectedResponseIn(directory: Path): String {
        if (Files.notExists(directory)) {
            return fail("Directory $directory does not exist")
        }
        try {
            Files.walk(directory).use { files ->
                return files
                    .filter { !Files.isDirectory(it) }
                    .findFirst()
                    .map { readFile(it) }
                    .orElseGet { fail("Directory $directory is empty") }
            }
        } catch (e: IOException) {
            return fail(e.message, e)
        }
    }

    private fun readFile(path: Path): String = try {
        Files.readString(path)
    } catch (e: IOException) {
        fail(e.message, e)
    }
}