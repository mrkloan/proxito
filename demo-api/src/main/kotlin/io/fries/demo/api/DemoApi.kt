package io.fries.demo.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication
@ConfigurationPropertiesScan
class DemoApi

fun main(args: Array<String>) {
    SpringApplication.run(DemoApi::class.java, *args)
}