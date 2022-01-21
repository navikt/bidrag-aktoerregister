package no.nav.bidrag.aktoerregister.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;
import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.bidrag.aktoerregister.service.SecurityTokenService;
import no.nav.bidrag.commons.web.CorrelationIdFilter;
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MQProperties.class})
@EnableJwtTokenValidation(ignore = {"org.springframework","org.springdoc"})
public class AktoerregisterConfiguration {

  private final static Logger logger = LoggerFactory.getLogger(AktoerregisterConfiguration.class);

  @Bean("base")
  public HttpHeaderRestTemplate baseRestTemplate() {
    HttpHeaderRestTemplate restTemplate = new HttpHeaderRestTemplate();
    restTemplate.addHeaderGenerator(CorrelationIdFilter.CORRELATION_ID_HEADER, () -> {
      var correlationID = CorrelationIdFilter.fetchCorrelationIdForThread();
      return Arrays.stream(correlationID.split("/")).findFirst().orElse(correlationID);
    });
    return restTemplate;
  }

  @Bean("pdl")
  public HttpHeaderRestTemplate pdlRestTemplate(@Value("${PDL_URL}") String pdlUrl, SecurityTokenService securityTokenService) {
    HttpHeaderRestTemplate pdlRestTemplate = baseRestTemplate();
    logger.info("Setting pdlUrl to: " + pdlUrl);
    pdlRestTemplate.setUriTemplateHandler(new RootUriTemplateHandler(pdlUrl));
    pdlRestTemplate.addHeaderGenerator("Tema", () -> "BID");
    pdlRestTemplate.getInterceptors().add(securityTokenService.generateBearerToken("pdlapi"));
    return pdlRestTemplate;
  }

  @Bean
  public CorrelationIdFilter correlationIdFilter() {
    return new CorrelationIdFilter();
  }
}
