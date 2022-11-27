package io.fries.api.test.proxy

import io.fries.api.test.ApiTestContext

fun interface ProxyServersFactory {
    fun create(testContext: ApiTestContext): ProxyServers
}