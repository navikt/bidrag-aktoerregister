package no.nav.bidrag.aktoerregister;

import no.nav.bidrag.aktoerregister.properties.MQProperties;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
@EnableJwtTokenValidation(ignore = {"org.springframework","org.springdoc"})
public class AktoerregisterConfiguration {

  @Autowired
  private MQProperties mqProperties;
}
