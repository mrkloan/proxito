package io.fries.proxito.core.proxy

class ProxyServers(private val servers: List<ProxyServer> = listOf()) : ProxyServer {

    override fun start() {
        servers.forEach(ProxyServer::start)
    }

    override fun stop() {
        servers.forEach(ProxyServer::stop)
    }

    operator fun plus(other: ProxyServers): ProxyServers = ProxyServers(servers + other.servers)
}