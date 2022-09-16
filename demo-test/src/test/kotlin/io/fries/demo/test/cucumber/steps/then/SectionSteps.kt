package io.fries.demo.test.cucumber.steps.then

import com.jayway.jsonpath.JsonPath
import io.cucumber.java.en.Then
import io.fries.demo.test.cucumber.steps.then.json.JsonArrayAssert.Companion.assertThatJsonArray
import io.fries.demo.test.cucumber.world.World
import net.minidev.json.JSONArray

class SectionSteps(private val world: World) {

    @Then("there are some sections with a physical mode {string}")
    fun then_there_are_some_sections_with_a_physical_mode(physicalMode: String) {
        val expectedSections = JsonPath
            .parse(world.filteredData())
            .read<JSONArray>("$.journeys[*].sections[?(@.display_informations.physical_mode == '$physicalMode')]")

        assertThatJsonArray(expectedSections)
            .describedAs("There are some sections with a physical mode $physicalMode")
            .isNotNull

        world.filteredData(expectedSections)
    }

    @Then("some of these sections have the network {string}")
    fun then_some_of_these_sections_have_the_network(network: String) {
        val expectedSections = JsonPath
            .parse(world.filteredData())
            .read<JSONArray>("$[?(@.display_informations.network == '$network')]")

        assertThatJsonArray(expectedSections)
            .describedAs("Some of these sections have the network $network")
            .isNotNull

        world.filteredData(expectedSections)
    }

    @Then("all of these sections have carbon emission data")
    fun then_all_of_these_sections_have_carbon_emission_data() {
        assertThatJsonArray(world.filteredData() as JSONArray)
            .describedAs("All of these sections have carbon emission data")
            .isNotNull
            .allSatisfyJson { section ->
                section
                    .assertNotNull<Any>("$.co2_emission.value")
                    .assertNotNull<Any>("$.co2_emission.unit")
            }
    }
}