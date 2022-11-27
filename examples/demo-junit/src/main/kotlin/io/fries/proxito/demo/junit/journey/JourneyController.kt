package io.fries.proxito.demo.junit.journey

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RestController
class JourneyController(
    private val properties: NavitiaProperties,
    restTemplateBuilder: RestTemplateBuilder
) {

    companion object {
        private val NAVITIA_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    }

    private val restTemplate: RestTemplate

    init {
        restTemplate = restTemplateBuilder
            .basicAuthentication(properties.token, "")
            .build()
    }

    @GetMapping("/journeys")
    fun journeys(origin: String?, destination: String?, departureDateTime: String?): ResponseEntity<String> {
        val datetime = ZonedDateTime.parse(departureDateTime).format(NAVITIA_DATE_TIME_FORMAT)
        val url =
            "${properties.endpoint}/journeys?from=$origin&to=$destination&datetime=$datetime&datetime_represents=departure"

        return restTemplate.getForEntity(url, String::class.java)
    }
}