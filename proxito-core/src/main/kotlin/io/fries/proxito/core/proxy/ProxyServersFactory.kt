package io.fries.proxito.core.proxy

import io.fries.proxito.core.ApiTestContext

fun interface ProxyServersFactory {
    fun create(testContext: ApiTestContext): ProxyServers
}