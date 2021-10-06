package no.nav.bidrag.aktoerregister;

import javax.annotation.PostConstruct;
import no.nav.bidrag.aktoerregister.jobs.TPSConsumerJob;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Bidrag Aktørregister", version = "0.2", description = "Inneholder adresse- og kontoinformasjon om aktører i Bidrassaker."))
@EnableConfigurationProperties()
public class AktoerregisterApplication {

    @Autowired
    private JobScheduler jobScheduler;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(AktoerregisterApplication.class, args)));
    }

    @PostConstruct
    public void startJobs() {
        jobScheduler.enqueue(TPSConsumerJob::execute);
    }
}
