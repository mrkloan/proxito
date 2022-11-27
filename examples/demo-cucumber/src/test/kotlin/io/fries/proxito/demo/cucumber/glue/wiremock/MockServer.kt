package io.fries.proxito.demo.cucumber.glue.wiremock

interface MockServer {
    fun start()
    fun stop()
}