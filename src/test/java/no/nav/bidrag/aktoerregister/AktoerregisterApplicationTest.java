package no.nav.bidrag.aktoerregister;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@OpenAPIDefinition(
    info =
        @Info(
            title = "Bidrag Aktørregister",
            version = "0.2",
            description = "Inneholder adresse- og kontoinformasjon om aktører i Bidrassaker."))
@EnableConfigurationProperties()
public class AktoerregisterApplicationTest {

  public static void main(String[] args) {
    SpringApplication.run(AktoerregisterApplicationTest.class, args);
  }
}
