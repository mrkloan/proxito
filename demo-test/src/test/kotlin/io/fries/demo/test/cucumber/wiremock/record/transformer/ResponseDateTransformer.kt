package io.fries.demo.test.cucumber.wiremock.record.transformer

import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.fries.demo.test.cucumber.wiremock.WireMockTransformerProperties
import io.fries.demo.test.cucumber.wiremock.replay.template.DateTemplate
import io.fries.demo.test.cucumber.world.WorldClock
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

class ResponseDateTransformer(
    private val worldClock: WorldClock,
    private val properties: WireMockTransformerProperties
) : StubMappingTransformer() {

    override fun transform(stubMapping: StubMapping, files: FileSource, parameters: Parameters?) = stubMapping.apply {
        val bodyFileName: String = response.bodyFileName
        val responseBody = files.getTextFileNamed(bodyFileName).readContentsAsString()
        val bodyTemplate = toBodyTemplate(responseBody)
        files.writeTextFile(bodyFileName, bodyTemplate)
    }

    private fun toBodyTemplate(body: String): String {
        return properties.pattern
            .matcher(body)
            .replaceAll { result -> toDateTemplate(result.group()) }
    }

    private fun toDateTemplate(responseDate: String): String {
        // The `replacement` pattern is intended to be written back into a text file, so it may contain unwanted additional escape chars.
        val unEscapedReplacement = properties.replacement.replace("\\", "")
        val responseTemporal = DateTimeFormatter.ofPattern(unEscapedReplacement).parse(responseDate)
        val now = worldClock.now()
        val responseZonedDateTime = toZonedDateTime(responseTemporal, now)
        val offset = Duration.between(now, responseZonedDateTime)

        return DateTemplate.template(properties.replacement, offset)
    }

    private fun toZonedDateTime(responseTemporal: TemporalAccessor, now: ZonedDateTime): ZonedDateTime = now
        .with({ LocalDate.from(responseTemporal) }.or { now.toLocalDate() })
        .with({ LocalTime.from(responseTemporal) }.or { now.toLocalTime() })
        .withZoneSameInstant({ ZoneId.from(responseTemporal) }.or { now.zone })

    private fun <T> (() -> T).or(defaultValue: () -> T): T = try {
        this.invoke()
    } catch (e: DateTimeException) {
        defaultValue.invoke()
    }

    override fun getName(): String = "response-date-transformer"
}