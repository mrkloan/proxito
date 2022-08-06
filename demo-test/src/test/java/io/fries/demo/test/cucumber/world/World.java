package io.fries.demo.test.cucumber.world;

import io.fries.demo.test.cucumber.world.request.RequestBuilder;
import io.fries.demo.test.cucumber.world.request.RequestTracker;
import io.fries.demo.test.cucumber.world.request.ServerProperties;
import io.fries.demo.test.cucumber.world.scenario.ScenarioId;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.http.HttpMethod.GET;

@Component
public class World {

    private final ServerProperties serverProperties;
    private final RestTemplate restTemplate;

    private ScenarioId scenarioId;
    private WorldClock clock;
    private RequestBuilder requestBuilder;
    private RequestTracker requestTracker;
    private ResponseEntity<String> response;
    private Object filteredData;

    public World(final ServerProperties serverProperties, final RestTemplateBuilder restTemplateBuilder) {
        this.serverProperties = serverProperties;
        this.restTemplate = restTemplateBuilder.build();
    }

    public void reset(final ScenarioId scenarioId) {
        this.scenarioId = scenarioId;

        clock = WorldClock.reset();
        requestBuilder = RequestBuilder.from(serverProperties);
        requestTracker = RequestTracker.empty();
        response = null;
        filteredData = null;
    }

    public ScenarioId scenarioId() {
        return scenarioId;
    }

    public WorldClock clock() {
        return clock;
    }

    public RequestBuilder request() {
        return requestBuilder;
    }

    public RequestTracker tracker() {
        return requestTracker;
    }

    public void performGet(final String endpoint) {
        try {
            final var requestEntity = requestBuilder.path(endpoint).build(GET);
            response = restTemplate.exchange(requestEntity, String.class);
        } catch (final Exception e) {
            fail(e.getMessage(), e);
        }
    }

    public ResponseEntity<String> response() {
        assertThat(response).as("response cannot be read before performing a request").isNotNull();
        return response;
    }

    public Object filteredData() {
        assertThat(response).as("filteredData cannot be read before performing a request").isNotNull();
        assertThat(filteredData).as("filteredData cannot be read before filtering the response").isNotNull();
        return filteredData;
    }

    public void filteredData(final Object filteredData) {
        this.filteredData = filteredData;
    }
}
