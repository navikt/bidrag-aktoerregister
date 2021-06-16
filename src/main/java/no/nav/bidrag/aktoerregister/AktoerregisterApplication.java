package no.nav.bidrag.aktoerregister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Bidrag Aktørregister", version = "0.2", description = "Inneholder adresse- og kontoinformasjon om aktører i Bidrassaker."))
public class AktoerregisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AktoerregisterApplication.class, args);
    }

}
