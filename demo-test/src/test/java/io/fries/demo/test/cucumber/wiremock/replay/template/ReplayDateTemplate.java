package io.fries.demo.test.cucumber.wiremock.replay.template;

import io.fries.demo.test.cucumber.world.WorldClock;
import org.springframework.stereotype.Component;
import wiremock.com.github.jknack.handlebars.Helper;
import wiremock.com.github.jknack.handlebars.Options;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class ReplayDateTemplate implements Helper<Object> {

    public static final String NAME = "date";

    private final WorldClock worldClock;

    public ReplayDateTemplate(final WorldClock worldClock) {
        this.worldClock = worldClock;
    }

    @Override
    public String apply(final Object context, final Options options) {
        final var pattern = options.<String>hash("pattern");
        final var offset = Optional.ofNullable(options.<String>hash("offset"))
                .map(Duration::parse)
                .orElse(Duration.ZERO);

        return worldClock.now()
                .plus(offset)
                .format(DateTimeFormatter.ofPattern(pattern));
    }
}
