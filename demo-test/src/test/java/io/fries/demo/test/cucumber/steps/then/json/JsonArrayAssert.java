package io.fries.demo.test.cucumber.steps.then.json;

import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonassert.impl.JsonAsserterImpl;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowingConsumer;

public class JsonArrayAssert extends AbstractListAssert<JsonArrayAssert, JSONArray, Object, ObjectAssert<Object>> {

    private JsonArrayAssert(final JSONArray actual) {
        super(actual, JsonArrayAssert.class);
    }

    public static JsonArrayAssert assertThatJsonArray(final JSONArray jsonArray) {
        return new JsonArrayAssert(jsonArray);
    }

    public JsonArrayAssert anySatisfyJson(final ThrowingConsumer<JsonAsserter> requirements) {
        map(JsonStringAsserter::of).anySatisfy(requirements);
        return this;
    }

    public JsonArrayAssert allSatisfyJson(final ThrowingConsumer<JsonAsserter> requirements) {
        map(JsonStringAsserter::of).allSatisfy(requirements);
        return this;
    }

    @Override
    protected ObjectAssert<Object> toAssert(final Object o, final String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JsonArrayAssert newAbstractIterableAssert(Iterable<?> iterable) {
        throw new UnsupportedOperationException();
    }

    private static class JsonStringAsserter extends JsonAsserterImpl {

        private final Object jsonObject;

        private JsonStringAsserter(final Object jsonObject) {
            super(jsonObject);
            this.jsonObject = jsonObject;
        }

        static JsonStringAsserter of(final Object object) {
            final var jsonObject = JsonPath.parse(object).json();
            return new JsonStringAsserter(jsonObject);
        }

        @Override
        public String toString() {
            return jsonObject.toString();
        }
    }
}
