package io.fries.demo.test.cucumber.wiremock.record.transformer

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class IdempotentStubIdTransformer(private val serverName: String) : StubMappingTransformer() {

    private val requestsCounter: AtomicInteger = AtomicInteger()
    private val previouslyAppliedOn: MutableSet<UUID> = mutableSetOf()

    override fun transform(stubMapping: StubMapping, files: FileSource, parameters: Parameters?) = stubMapping.apply {
        if (previouslyAppliedOn.contains(id)) {
            return this
        }

        val idempotentId = generateIdempotentUUID()
        updateResponseBody(files, idempotentId)
        id = idempotentId
        name = serverName

        previouslyAppliedOn.add(idempotentId)
    }

    private fun generateIdempotentUUID(): UUID = "$serverName-${requestsCounter.incrementAndGet()}"
        .toByteArray(StandardCharsets.UTF_8)
        .let(UUID::nameUUIDFromBytes)

    private fun StubMapping.updateResponseBody(files: FileSource, idempotentId: UUID) = apply {
        val bodyFileName = response.bodyFileName ?: return this
        val idempotentFileName = bodyFileName
            .replace(name, serverName)
            .replace(id.toString(), idempotentId.toString())

        val responseBody = files.getTextFileNamed(bodyFileName).readContentsAsString()
        files.deleteFile(bodyFileName)
        files.writeTextFile(idempotentFileName, responseBody)

        response = ResponseDefinitionBuilder.like(response)
            .withBodyFile(idempotentFileName)
            .build()
    }

    override fun getName(): String = "idempotent-stub-id-transformer"
}