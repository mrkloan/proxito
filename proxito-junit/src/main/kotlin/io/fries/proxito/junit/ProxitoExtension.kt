package io.fries.proxito.junit

import io.fries.proxito.core.proxy.ProxyServers
import io.fries.proxito.core.proxy.ProxyServersFactory
import io.fries.proxito.junit.context.JunitContext
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension

class ProxitoExtension : BeforeEachCallback, AfterEachCallback {

    private lateinit var proxyServers: ProxyServers

    override fun beforeEach(extensionContext: ExtensionContext) {
        val applicationContext = SpringExtension.getApplicationContext(extensionContext)
        val proxyServersFactory = applicationContext.getBean(ProxyServersFactory::class.java)

        proxyServers = proxyServersFactory.create(
            JunitContext(
                className = extensionContext.requiredTestClass.canonicalName,
                testName = extensionContext.requiredTestMethod.name
            )
        )
        proxyServers.start()
    }

    override fun afterEach(extensionContext: ExtensionContext) {
        proxyServers.stop()
    }
}