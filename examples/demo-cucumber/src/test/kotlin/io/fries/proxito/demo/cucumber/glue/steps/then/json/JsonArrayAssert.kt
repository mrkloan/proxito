package io.fries.proxito.demo.cucumber.glue.steps.then.json

import com.jayway.jsonassert.JsonAsserter
import com.jayway.jsonassert.impl.JsonAsserterImpl
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import org.assertj.core.api.AbstractListAssert
import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.ThrowingConsumer

class JsonArrayAssert private constructor(actual: JSONArray) :
    AbstractListAssert<io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert, JSONArray, Any, ObjectAssert<Any>>(actual, io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert::class.java) {

    companion object {
        fun assertThatJsonArray(jsonArray: JSONArray): io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert {
            return io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert(jsonArray)
        }
    }

    fun anySatisfyJson(requirements: ThrowingConsumer<JsonAsserter>) = apply {
        map<io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert.JsonStringAsserter, RuntimeException> {
            io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert.JsonStringAsserter.Companion.of(
                it
            )
        }.anySatisfy(requirements)
    }

    fun allSatisfyJson(requirements: ThrowingConsumer<JsonAsserter>) = apply {
        map<io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert.JsonStringAsserter, RuntimeException> {
            io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert.JsonStringAsserter.Companion.of(
                it
            )
        }.allSatisfy(requirements)
    }

    private class JsonStringAsserter private constructor(private val jsonObject: Any) : JsonAsserterImpl(jsonObject) {

        companion object {
            fun of(value: Any): io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert.JsonStringAsserter = JsonPath
                .parse(value)
                .json<Any>()
                .let { io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert.JsonStringAsserter(it) }
        }

        override fun toString(): String = jsonObject.toString()
    }

    override fun toAssert(o: Any, s: String): ObjectAssert<Any> {
        throw UnsupportedOperationException()
    }

    override fun newAbstractIterableAssert(iterable: Iterable<*>?): io.fries.proxito.demo.cucumber.glue.steps.then.json.JsonArrayAssert {
        throw UnsupportedOperationException()
    }
}