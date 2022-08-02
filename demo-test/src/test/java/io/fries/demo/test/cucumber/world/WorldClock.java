package io.fries.demo.test.cucumber.world;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

public class WorldClock {

    private Supplier<ZonedDateTime> clock;

    private WorldClock() {
        clock = ZonedDateTime::now;
    }

    public static WorldClock reset() {
        return new WorldClock();
    }

    public void fixedAt(final ZonedDateTime zonedDateTime) {
        clock = () -> zonedDateTime;
    }

    public ZonedDateTime now() {
        return clock.get();
    }
}
