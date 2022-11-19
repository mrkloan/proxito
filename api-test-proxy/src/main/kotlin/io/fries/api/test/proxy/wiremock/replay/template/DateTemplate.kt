package io.fries.api.test.proxy.wiremock.replay.template

import org.springframework.stereotype.Component
import wiremock.com.github.jknack.handlebars.Helper
import wiremock.com.github.jknack.handlebars.Options
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class DateTemplate(val clock: () -> ZonedDateTime) : Helper<Any?> {

    companion object {
        const val NAME = "date"

        fun template(pattern: String, offset: Duration): String {
            return "{{{$NAME pattern='$pattern' offset='$offset'}}}"
        }
    }

    override fun apply(context: Any?, options: Options): String {
        val pattern = DateTimeFormatter.ofPattern(options.hash("pattern"))
        val offset = Duration.parse(options.hash("offset"))

        return clock
            .invoke()
            .plus(offset)
            .format(pattern)
    }
}