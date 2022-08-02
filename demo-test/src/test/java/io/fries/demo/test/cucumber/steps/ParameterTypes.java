package io.fries.demo.test.cucumber.steps;

import io.cucumber.java.ParameterType;
import io.fries.demo.test.cucumber.world.QueryParameter;
import io.fries.demo.test.cucumber.world.World;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.time.ZonedDateTime;

public class ParameterTypes {

    @Autowired
    private World world;

    @ParameterType("(.*)=(.*)")
    public QueryParameter queryParameter(final String name, final String value) {
        return new QueryParameter(name, value);
    }

    @ParameterType("\\d{2}:\\d{2}")
    public ZonedDateTime zonedDateTime(final String time) {
        return world.clock()
                .now()
                .with(LocalTime.parse(time));
    }
}
