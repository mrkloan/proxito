package io.fries.proxito.demo.cucumber.glue.wiremock.replay.template

import io.fries.proxito.demo.cucumber.glue.world.World
import org.springframework.stereotype.Component
import wiremock.com.github.jknack.handlebars.Helper
import wiremock.com.github.jknack.handlebars.Options
import java.time.Duration
import java.time.format.DateTimeFormatter

@Component
class DateTemplate(private val world: World) : Helper<Any?> {

    companion object {
        const val NAME = "date"

        fun template(pattern: String, offset: Duration): String {
            return "{{{$NAME pattern='$pattern' offset='$offset'}}}"
        }
    }

    override fun apply(context: Any?, options: Options): String {
        val pattern = DateTimeFormatter.ofPattern(options.hash("pattern"))
        val offset = Duration.parse(options.hash("offset"))

        return world.clock()
            .now()
            .plus(offset)
            .format(pattern)
    }
}