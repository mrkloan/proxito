package io.fries.demo.api.journey;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class JourneyController {

    private static final DateTimeFormatter NAVITIA_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private final NavitiaProperties properties;
    private final RestTemplate restTemplate;

    public JourneyController(final NavitiaProperties properties, final RestTemplateBuilder restTemplateBuilder) {
        this.properties = properties;
        this.restTemplate = restTemplateBuilder
                .basicAuthentication(properties.token(), "")
                .build();
    }

    @GetMapping("/journeys")
    public ResponseEntity<String> journeys(final String origin, final String destination, final String departureDateTime) {
        final String url = String.format(
                "%s/journeys?from=%s&to=%s&datetime=%s&datetime_represents=departure",
                properties.endpoint(),
                origin,
                destination,
                ZonedDateTime.parse(departureDateTime).format(NAVITIA_DATE_TIME_FORMAT)
        );

        return restTemplate.getForEntity(url, String.class);
    }
}
