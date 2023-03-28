package no.nav.bidrag.aktoerregister.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import no.nav.bidrag.aktoerregister.properties.MQProperties
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.DefaultCorsFilter
import no.nav.bidrag.commons.web.UserMdcFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@OpenAPIDefinition(
    info = Info(
        title = "bidrag-aktørregister",
        version = "v1",
        description = "Inneholder adresse- og kontoinformasjon om aktører i Bidragssaker."
    ), security = [SecurityRequirement(name = "bearer-key")]
)
@SecurityScheme(bearerFormat = "JWT", name = "bearer-key", scheme = "bearer", type = SecuritySchemeType.HTTP)
@EnableRetry
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@EnableConfigurationProperties(MQProperties::class)
@Import(CorrelationIdFilter::class, DefaultCorsFilter::class, UserMdcFilter::class)
class DefaultConfiguration