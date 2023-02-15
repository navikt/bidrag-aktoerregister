package no.nav.bidrag.aktoerregister;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@OpenAPIDefinition(
    info =
        @Info(
            title = "Bidrag Aktørregister",
            version = "1.0",
            description = "Inneholder adresse- og kontoinformasjon om aktører i Bidragssaker."))
@EnableConfigurationProperties()
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class AktoerregisterApplication {

  public static void main(String[] args) {
    SpringApplication.run(AktoerregisterApplication.class, args);
  }
}
