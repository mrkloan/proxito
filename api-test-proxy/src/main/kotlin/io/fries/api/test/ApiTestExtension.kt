package io.fries.api.test

import io.fries.api.test.proxy.ProxyServers
import io.fries.api.test.proxy.ProxyServersFactory
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension

data class ApiTestContext(
    private val recordPath: String,
    private val className: String,
    private val testName: String
) {
    override fun toString(): String = "$recordPath/$className/$testName"
}

class ApiTestExtension : BeforeEachCallback, AfterEachCallback {

    private lateinit var proxyServers: ProxyServers

    override fun beforeEach(extensionContext: ExtensionContext) {
        val applicationContext = SpringExtension.getApplicationContext(extensionContext)
        val apiTestProperties = applicationContext.getBean(ApiTestProperties::class.java)
        val proxyServersFactory = applicationContext.getBean(ProxyServersFactory::class.java)

        proxyServers = proxyServersFactory.create(
            ApiTestContext(
                recordPath = apiTestProperties.recordPath,
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