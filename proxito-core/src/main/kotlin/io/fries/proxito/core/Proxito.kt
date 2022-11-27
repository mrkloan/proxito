package io.fries.proxito.core

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ActiveProfilesResolver
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ComponentScan("io.fries.proxito")
@ConfigurationPropertiesScan("io.fries.proxito")
@ActiveProfiles(resolver = ProxitoProfilesResolver::class)
@ExtendWith(ProxitoExtension::class)
@Target(CLASS)
@Retention(RUNTIME)
annotation class Proxito(
    val profiles: Array<String> = ["replay"]
)

class ProxitoProfilesResolver : ActiveProfilesResolver {
    override fun resolve(testClass: Class<*>): Array<String> = testClass
        .declaredAnnotations
        .filterIsInstance<Proxito>()
        .flatMap { proxito ->
            proxito.profiles.map { profile ->
                profile.lowercase().trim()
            }
        }
        .toTypedArray()
}