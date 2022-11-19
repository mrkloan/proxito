package io.fries.api.test.proxy

class ProxyServers(private val servers: List<ProxyServer> = listOf()) : ProxyServer {

    override fun start() {
        servers.forEach(ProxyServer::start)
    }

    override fun stop() {
        servers.forEach(ProxyServer::stop)
    }
}