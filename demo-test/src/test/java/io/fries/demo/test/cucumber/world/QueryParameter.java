package io.fries.demo.test.cucumber.world;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public record QueryParameter(
        String name,
        String value
) {

    public QueryParameter(final String name, final String value) {
        this.name = requireNonNull(name, "name cannot be null");
        this.value = URLEncoder.encode(
                requireNonNull(value, "value cannot be null"),
                UTF_8
        );
    }
}
