package io.fries.proxito.core.proxy

import io.fries.proxito.core.context.ProxitoContext

fun interface ProxyServersFactory {
    fun create(context: ProxitoContext): ProxyServers
}