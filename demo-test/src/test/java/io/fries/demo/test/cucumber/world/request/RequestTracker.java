package io.fries.demo.test.cucumber.world.request;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

import java.util.HashMap;
import java.util.Map;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestTracker {

    private final Map<String, Response> responseByUrl;

    private RequestTracker(final Map<String, Response> responseByUrl) {
        this.responseByUrl = responseByUrl;
    }

    public static RequestTracker empty() {
        return new RequestTracker(new HashMap<>());
    }

    public void track(final Request request, final Response response) {
        responseByUrl.put(request.getUrl(), response);
    }

    public void assertThatAllRequestsWereRecorded() {
        final var unrecordedRequests = responseByUrl.entrySet().stream()
                .filter(not(entry -> entry.getValue().wasConfigured()))
                .map(Map.Entry::getKey)
                .collect(toUnmodifiableSet());

        assertThat(unrecordedRequests)
                .as("Some unrecorded requests were tracked")
                .isEmpty();
    }
}
