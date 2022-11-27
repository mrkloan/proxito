package io.fries.proxito.core.context

fun interface ProxitoContext {
    fun path(): String
}

data class JunitContext(
    private val className: String,
    private val testName: String,
) : ProxitoContext {
    override fun path(): String = "$className/$testName"
}