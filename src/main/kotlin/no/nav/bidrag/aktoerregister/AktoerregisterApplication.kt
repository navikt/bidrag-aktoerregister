package no.nav.bidrag.aktoerregister

import io.github.oshai.KotlinLogging
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties

val SECURE_LOGGER = KotlinLogging.logger { "secureLogger" }

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
@EnableConfigurationProperties
@EnableJwtTokenValidation(ignore = ["org.springframework", "org.springdoc"])
class AktoerregisterApplication

fun main(args: Array<String>) {
    SpringApplication.run(AktoerregisterApplication::class.java, *args)
}
