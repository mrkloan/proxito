package io.fries.api.test

import io.fries.api.test.ApiTestMode.REPLAY
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ActiveProfilesResolver
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ComponentScan("io.fries.api.test")
@ConfigurationPropertiesScan("io.fries.api.test")
@ActiveProfiles(resolver = ApiTestProfileResolver::class)
@ExtendWith(ApiTestExtension::class)
@Target(CLASS)
@Retention(RUNTIME)
annotation class ApiTest(
    val mode: ApiTestMode = REPLAY
)

enum class ApiTestMode {
    /** Act as an HTTP proxy and record the partner's response. */
    RECORD,

    /** Act as an HTTP stub, never calling the actual partner, and provide the recorded responses. */
    REPLAY
}

class ApiTestProfileResolver : ActiveProfilesResolver {
    override fun resolve(testClass: Class<*>): Array<String> = testClass
        .declaredAnnotations
        .filterIsInstance<ApiTest>()
        .map { it.mode.name.lowercase() }
        .toTypedArray()
}