package no.nav.bidrag.aktoerregister;

import no.nav.bidrag.aktoerregister.properties.MQProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class AktoerregisterConfiguration {

  @Autowired
  private MQProperties mqProperties;
}
