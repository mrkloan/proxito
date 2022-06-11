package io.fries.demo.api.datetime;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("demo.datetime")
@ConstructorBinding
public record DateTimeProperties(
        String endpoint,
        String timezone
) {

    public String url() {
        return String.format("%s/%s", endpoint, timezone);
    }
}
