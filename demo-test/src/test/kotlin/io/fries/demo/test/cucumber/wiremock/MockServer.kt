package io.fries.demo.test.cucumber.wiremock

interface MockServer {
    fun start()
    fun stop()
}