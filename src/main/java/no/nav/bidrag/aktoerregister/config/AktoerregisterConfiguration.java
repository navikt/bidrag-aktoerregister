package no.nav.bidrag.aktoerregister.config;

import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MQProperties.class})
@EnableJwtTokenValidation(ignore = {"org.springframework", "org.springdoc"})
public class AktoerregisterConfiguration {}
