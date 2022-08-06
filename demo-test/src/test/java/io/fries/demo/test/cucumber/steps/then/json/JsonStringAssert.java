package io.fries.demo.test.cucumber.steps.then.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.AbstractAssert;

import java.util.Set;
import java.util.stream.StreamSupport;

import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonStringAssert extends AbstractAssert<JsonStringAssert, String> {

    private final ObjectMapper objectMapper;

    private JsonStringAssert(final String actual) {
        super(actual, JsonStringAssert.class);

        this.objectMapper = new ObjectMapper()
                .configure(ORDER_MAP_ENTRIES_BY_KEYS, true)
                .configure(WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule());
    }

    public static JsonStringAssert assertThatJsonString(final String actual) {
        return new JsonStringAssert(actual);
    }

    public JsonStringAssert isEqualToIgnoringFields(final String expected, final Set<String> fieldsToIgnore) {
        final var actualJsonNode = prepareJsonNode(actual, fieldsToIgnore);
        final var expectedJsonNode = prepareJsonNode(expected, fieldsToIgnore);

        assertThat(actualJsonNode.toPrettyString()).isEqualTo(expectedJsonNode.toPrettyString());
        return this;
    }

    private JsonNode prepareJsonNode(final String jsonString, final Set<String> fieldsToIgnore) {
        try {
            final var jsonNode = objectMapper.readTree(jsonString);
            return sortArraysOf(ignoreFieldsOf(jsonNode, fieldsToIgnore));
        } catch (final JsonProcessingException e) {
            return fail(e.getMessage(), e);
        }
    }

    private JsonNode ignoreFieldsOf(final JsonNode currentNode, final Set<String> fieldsToIgnore) {
        if (currentNode.isArray()) {
            currentNode.forEach(jsonNode -> ignoreFieldsOf(jsonNode, fieldsToIgnore));
        } else if (currentNode.isObject()) {
            currentNode.fields().forEachRemaining(field -> {
                if (fieldsToIgnore.contains(field.getKey())) {
                    ((ObjectNode) currentNode).replace(
                            field.getKey(),
                            objectMapper.convertValue("__value_ignored__", JsonNode.class)
                    );
                } else {
                    ignoreFieldsOf(field.getValue(), fieldsToIgnore);
                }
            });
        }

        return currentNode;
    }

    private JsonNode sortArraysOf(final JsonNode currentNode) {
        currentNode.fields().forEachRemaining(field -> sortArraysOf(field.getValue()));

        if (currentNode.isArray()) {
            final var arrayNode = (ArrayNode) currentNode;
            final var sortedArray = sort(arrayNode);
            arrayNode.removeAll().addAll(sortedArray);
        }

        return currentNode;
    }

    private ArrayNode sort(final ArrayNode arrayNode) {
        final var sortedNodes = StreamSupport.stream(arrayNode.spliterator(), false)
                .sorted(comparing(JsonNode::toString))
                .toList();

        return objectMapper
                .createArrayNode()
                .addAll(sortedNodes);
    }
}
