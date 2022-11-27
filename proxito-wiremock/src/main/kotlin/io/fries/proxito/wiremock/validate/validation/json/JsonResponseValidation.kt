package io.fries.proxito.wiremock.validate.validation.json

import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.fries.proxito.core.ApiTestContext
import io.fries.proxito.wiremock.JsonValidationProperties
import io.fries.proxito.wiremock.ROOT_DIRECTORY
import io.fries.proxito.wiremock.replay.template.DateTemplate
import io.fries.proxito.wiremock.validate.validation.json.JsonStringAssert.Companion.assertThatJsonString
import org.assertj.core.api.Assertions.fail
import wiremock.com.github.jknack.handlebars.Handlebars
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class JsonResponseValidation(
    private val apiTestContext: ApiTestContext,
    private val serverName: String,
    private val properties: JsonValidationProperties,
    private val dateTemplate: DateTemplate
) : StubMappingTransformer() {

    override fun transform(stubMapping: StubMapping, files: FileSource, parameters: Parameters?) = stubMapping.apply {
        val bodyFileName = stubMapping.response.bodyFileName
        val actualResponse = bodyFileName
            .let { files.getTextFileNamed(it) }
            .readContentsAsString()

        files.deleteFile(bodyFileName)

        val responsesDirectory = Path.of("$ROOT_DIRECTORY/$apiTestContext/$serverName/__files")
        val templatedResponse = readExpectedResponseTemplateIn(responsesDirectory)
        val expectedResponse = Handlebars()
            .registerHelper(DateTemplate.NAME, dateTemplate)
            .compileInline(templatedResponse)
            .apply(Any())

        assertThatJsonString(actualResponse).isEqualToIgnoringFields(expectedResponse, properties.fieldsToIgnore)
    }

    private fun readExpectedResponseTemplateIn(directory: Path): String {
        if (Files.notExists(directory)) {
            return fail("Directory $directory does not exist")
        }
        try {
            Files.walk(directory).use { files ->
                return files
                    .filter { !Files.isDirectory(it) }
                    .findFirst()
                    .map { Files.readString(it) }
                    .orElseGet { fail("Directory $directory is empty") }
            }
        } catch (e: IOException) {
            return fail(e.message, e)
        }
    }

    override fun getName(): String = "json-response-validation"
}