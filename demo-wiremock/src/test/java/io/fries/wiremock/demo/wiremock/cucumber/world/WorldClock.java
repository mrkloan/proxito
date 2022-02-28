package io.fries.wiremock.demo.wiremock.cucumber.world;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

@Component
public class WorldClock {

    private Supplier<ZonedDateTime> clock;

    public WorldClock() {
        reset();
    }

    public final void reset() {
        clock = ZonedDateTime::now;
    }

    public void fixedAt(final ZonedDateTime zonedDateTime) {
        clock = () -> zonedDateTime;
    }

    public ZonedDateTime now() {
        return clock.get();
    }
}
