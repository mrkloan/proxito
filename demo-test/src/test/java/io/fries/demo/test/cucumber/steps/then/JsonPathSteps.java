package io.fries.demo.test.cucumber.steps.then;

import io.cucumber.java.en.Then;
import io.fries.demo.test.cucumber.world.World;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class JsonPathSteps {

    @Autowired
    private World world;

    @Then("{string} has value {string}")
    public void then_json_path_has_value(final String jsonPath, final String expectedValue) {
        world.assertThat(jsonPath(jsonPath).value(equalTo(expectedValue)));
    }
}
