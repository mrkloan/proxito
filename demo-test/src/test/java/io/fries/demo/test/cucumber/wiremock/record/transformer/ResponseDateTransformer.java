package io.fries.demo.test.cucumber.wiremock.record.transformer;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.fries.demo.test.cucumber.wiremock.WireMockProperties.WireMockTransformerProperties;
import io.fries.demo.test.cucumber.wiremock.replay.template.DateTemplate;
import io.fries.demo.test.cucumber.world.WorldClock;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.function.Supplier;

public class ResponseDateTransformer extends StubMappingTransformer {

    private final WorldClock worldClock;
    private final WireMockTransformerProperties properties;

    public ResponseDateTransformer(final WorldClock worldClock, final WireMockTransformerProperties properties) {
        this.worldClock = worldClock;
        this.properties = properties;
    }

    @Override
    public StubMapping transform(final StubMapping stubMapping, final FileSource files, final Parameters parameters) {
        final var response = stubMapping.getResponse();
        final var bodyFileName = response.getBodyFileName();
        final var responseBody = files.getTextFileNamed(bodyFileName).readContentsAsString();
        final var bodyTemplate = toBodyTemplate(responseBody);

        files.writeTextFile(bodyFileName, bodyTemplate);

        return stubMapping;
    }

    private String toBodyTemplate(final String body) {
        return properties.pattern()
                .matcher(body)
                .replaceAll(result -> toDateTemplate(result.group()));
    }

    private String toDateTemplate(final String responseDate) {
        // The `replacement` pattern is intended to be written back into a text file, so it may contain unwanted additional escape chars.
        final var unEscapedReplacement = properties.replacement().replace("\\", "");
        final var responseTemporal = DateTimeFormatter.ofPattern(unEscapedReplacement).parse(responseDate);

        final var now = worldClock.now();
        final var responseZonedDateTime = toZonedDateTime(responseTemporal, now);

        final var offset = Duration.between(now, responseZonedDateTime);
        return DateTemplate.template(properties.replacement(), offset);
    }

    private ZonedDateTime toZonedDateTime(final TemporalAccessor responseTemporal, final ZonedDateTime now) {
        return now
                .with(mapOrDefault(() -> LocalDate.from(responseTemporal), now::toLocalDate))
                .with(mapOrDefault(() -> LocalTime.from(responseTemporal), now::toLocalTime))
                .withZoneSameInstant(mapOrDefault(() -> ZoneId.from(responseTemporal), now::getZone));
    }

    private <T> T mapOrDefault(final Supplier<T> mappedValue, final Supplier<T> defaultValue) {
        try {
            return mappedValue.get();
        } catch (final DateTimeException e) {
            return defaultValue.get();
        }
    }

    @Override
    public String getName() {
        return "response-date-transformer";
    }
}
