package io.fries.demo.test.cucumber.steps.then;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import io.fries.demo.test.cucumber.world.World;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionSteps {

    @Autowired
    private World world;

    @Then("there are some sections with a physical mode {string}")
    public void then_there_are_some_sections_with_a_physical_mode(final String physicalMode) {
        final JSONArray expectedSections = JsonPath
                .parse(world.response().getBody())
                .read(format("$.journeys[*].sections[?(@.display_informations.physical_mode == '%s')]", physicalMode));

        assertThat(expectedSections)
                .as(format("There are some sections with a physical mode %s", physicalMode))
                .isNotNull();

        world.filteredData(expectedSections);
    }

    @Then("some of these sections have the network {string}")
    public void then_some_of_these_sections_have_the_network(final String network) {
        final JSONArray expectedSections = JsonPath
                .parse(world.response().getBody())
                .read(format("$[?(@.display_informations.network == '%s')]", network));

        assertThat(expectedSections)
                .as(format("Some of these sections have the network %s", network))
                .isNotNull();

        world.filteredData(expectedSections);
    }
}
