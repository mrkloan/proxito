package io.fries.demo.test.cucumber.steps.then.json

import com.jayway.jsonassert.JsonAsserter
import com.jayway.jsonassert.impl.JsonAsserterImpl
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import org.assertj.core.api.AbstractListAssert
import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.ThrowingConsumer

class JsonArrayAssert private constructor(actual: JSONArray) :
    AbstractListAssert<JsonArrayAssert, JSONArray, Any, ObjectAssert<Any>>(actual, JsonArrayAssert::class.java) {

    companion object {
        fun assertThatJsonArray(jsonArray: JSONArray): JsonArrayAssert {
            return JsonArrayAssert(jsonArray)
        }
    }

    fun anySatisfyJson(requirements: ThrowingConsumer<JsonAsserter>) = apply {
        map<JsonStringAsserter, RuntimeException> { JsonStringAsserter.of(it) }.anySatisfy(requirements)
    }

    fun allSatisfyJson(requirements: ThrowingConsumer<JsonAsserter>) = apply {
        map<JsonStringAsserter, RuntimeException> { JsonStringAsserter.of(it) }.allSatisfy(requirements)
    }

    private class JsonStringAsserter private constructor(private val jsonObject: Any) : JsonAsserterImpl(jsonObject) {

        companion object {
            fun of(value: Any): JsonStringAsserter = JsonPath
                .parse(value)
                .json<Any>()
                .let { JsonStringAsserter(it) }
        }

        override fun toString(): String = jsonObject.toString()
    }

    override fun toAssert(o: Any, s: String): ObjectAssert<Any> {
        throw UnsupportedOperationException()
    }

    override fun newAbstractIterableAssert(iterable: Iterable<*>?): JsonArrayAssert {
        throw UnsupportedOperationException()
    }
}