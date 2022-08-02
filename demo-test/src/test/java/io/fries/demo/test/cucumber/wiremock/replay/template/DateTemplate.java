package io.fries.demo.test.cucumber.wiremock.replay.template;

import io.fries.demo.test.cucumber.world.World;
import org.springframework.stereotype.Component;
import wiremock.com.github.jknack.handlebars.Helper;
import wiremock.com.github.jknack.handlebars.Options;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

@Component
public class DateTemplate implements Helper<Object> {

    public static final String NAME = "date";

    private final World world;

    public DateTemplate(final World world) {
        this.world = world;
    }

    public static String template(final String pattern, final Duration offset) {
        return String.format("{{{%s pattern='%s' offset='%s'}}}", NAME, pattern, offset);
    }

    @Override
    public String apply(final Object context, final Options options) {
        final var pattern = DateTimeFormatter.ofPattern(options.hash("pattern"));
        final var offset = Duration.parse(options.hash("offset"));

        return world.clock()
                .now()
                .plus(offset)
                .format(pattern);
    }
}
