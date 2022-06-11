package io.fries.demo.api.datetime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DateTimeController {

    private final DateTimeProperties properties;
    private final RestTemplate restTemplate;

    public DateTimeController(final DateTimeProperties properties, final RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/datetime")
    public DateTime dateTime() {
        return restTemplate.getForObject(properties.url(), DateTime.class);
    }
}
