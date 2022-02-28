package io.fries.wiremock.demo.wiremock.cucumber.world;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Component
public class World {

    private final MockMvc mockMvc;

    private ResultActions response;

    public World(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public void performGet(final String endpoint) {
        try {
            response = mockMvc.perform(get(endpoint));
        } catch (final Exception e) {
            fail(e.getMessage(), e);
        }
    }

    public void assertThat(final ResultMatcher matcher) {
        try {
            response = response.andExpect(matcher);
        } catch (final Exception e) {
            fail(e.getMessage(), e);
        }
    }
}
