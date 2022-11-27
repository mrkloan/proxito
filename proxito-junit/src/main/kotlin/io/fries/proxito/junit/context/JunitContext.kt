package io.fries.proxito.junit.context

import io.fries.proxito.core.context.ProxitoContext

data class JunitContext(
    private val className: String,
    private val testName: String,
) : ProxitoContext {
    override fun path(): String = "$className/$testName"
}