package io.fries.api.test.proxy.wiremock.validate.validation.json

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail

class JsonStringAssert private constructor(actual: String?) :
    AbstractAssert<JsonStringAssert?, String?>(actual, JsonStringAssert::class.java) {

    private val objectMapper: ObjectMapper = ObjectMapper()
        .enable(ORDER_MAP_ENTRIES_BY_KEYS)
        .disable(WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(JavaTimeModule())

    companion object {
        fun assertThatJsonString(actual: String?): JsonStringAssert = JsonStringAssert(actual)
    }

    fun isEqualToIgnoringFields(expected: String, fieldsToIgnore: Set<String>) = apply {
        val actualJsonNode = actual.prepareJsonNode(fieldsToIgnore)
        val expectedJsonNode = expected.prepareJsonNode(fieldsToIgnore)

        assertThat(actualJsonNode.toPrettyString()).isEqualTo(expectedJsonNode.toPrettyString())
    }

    private fun String?.prepareJsonNode(fieldsToIgnore: Set<String>): JsonNode = try {
        objectMapper
            .readTree(this)
            .ignoreFields(fieldsToIgnore)
            .sortArrays()
    } catch (e: JsonProcessingException) {
        fail(e.message, e)
    }

    private fun JsonNode.ignoreFields(fieldsToIgnore: Set<String>): JsonNode = apply {
        when (this) {
            is ArrayNode -> forEach { jsonNode -> jsonNode.ignoreFields(fieldsToIgnore) }
            is ObjectNode -> fields().forEachRemaining { (key, value) ->
                if (fieldsToIgnore.contains(key)) {
                    this.replace(key, objectMapper.convertValue("__value_ignored__", JsonNode::class.java))
                } else {
                    value.ignoreFields(fieldsToIgnore)
                }
            }
        }
    }

    private fun JsonNode.sortArrays(): JsonNode = apply {
        this.fields().forEachRemaining { (_, value) -> value.sortArrays() }
        if (isArray) {
            val arrayNode = this as ArrayNode
            val sortedArray = arrayNode.sort()
            arrayNode.removeAll().addAll(sortedArray)
        }
    }

    private fun ArrayNode.sort(): ArrayNode = objectMapper
        .createArrayNode()
        .addAll(sortedWith(Comparator.comparing { it.toString() }))
}