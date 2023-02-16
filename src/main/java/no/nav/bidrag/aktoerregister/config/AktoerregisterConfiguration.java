package no.nav.bidrag.aktoerregister.config;

import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.commons.web.DefaultCorsFilter;
import no.nav.bidrag.commons.web.MdcFilter;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({MQProperties.class})
@EnableJwtTokenValidation(ignore = {"org.springframework", "org.springdoc"})
@Import({DefaultCorsFilter.class, MdcFilter.class})
public class AktoerregisterConfiguration {}
