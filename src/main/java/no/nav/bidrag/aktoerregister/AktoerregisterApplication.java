package no.nav.bidrag.aktoerregister;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Bidrag Aktørregister", version = "0.4", description = "Inneholder adresse- og kontoinformasjon om aktører i Bidrassaker."))
@EnableConfigurationProperties()
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class AktoerregisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AktoerregisterApplication.class, args);
    }
}
