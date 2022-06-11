package io.fries.demo.test.cucumber.wiremock.replay;

import io.fries.demo.test.cucumber.world.WorldClock;
import org.springframework.stereotype.Component;
import wiremock.com.github.jknack.handlebars.Helper;
import wiremock.com.github.jknack.handlebars.Options;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class ReplayDateFormatTemplate implements Helper<String> {

    public static final String NAME = "date-format";

    private final WorldClock worldClock;

    public ReplayDateFormatTemplate(final WorldClock worldClock) {
        this.worldClock = worldClock;
    }

    @Override
    public String apply(final String dateFormat, final Options options) {
        final var offset = Optional.ofNullable(options.<String>param(0, null))
                .map(Duration::parse)
                .orElse(Duration.ZERO);

        return worldClock.now()
                .plus(offset)
                .format(DateTimeFormatter.ofPattern(dateFormat));
    }
}
