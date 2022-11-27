package io.fries.proxito.demo.junit.journey

import io.fries.proxito.core.Proxito
import io.fries.proxito.core.context.ProxitoClock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus.OK
import org.springframework.web.util.DefaultUriBuilderFactory
import java.time.ZonedDateTime

@Proxito
internal class JourneyControllerTest {

    @Autowired
    private lateinit var clock: ProxitoClock

    private lateinit var testRestTemplate: TestRestTemplate

    @BeforeEach
    internal fun setUp() {
        testRestTemplate = TestRestTemplate().apply {
            setUriTemplateHandler(DefaultUriBuilderFactory("http://127.0.0.1:9000"))
        }
    }

    @Test
    internal fun should_find_subways_operated_by_ratp() {
        val departureDate = clock.now().toLocalDate()
        val response = testRestTemplate.getForEntity(
            "/api/journeys?origin=2.29460;48.87358&destination=2.34893;48.85720&departureDateTime=${departureDate}T10:00:00Z",
            String::class.java
        )

        assertThat(response).describedAs("API should respond").isNotNull
        assertThat(response.statusCode).describedAs("Response should be 200 OK").isEqualTo(OK)
        assertThat(response.body).describedAs("Response body should not be null").isNotNull
    }
}