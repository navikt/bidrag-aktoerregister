package no.nav.bidrag.aktoerregister;

import javax.annotation.PostConstruct;
import no.nav.bidrag.aktoerregister.jobs.TPSConsumerJob;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Bidrag Aktørregister", version = "0.2", description = "Inneholder adresse- og kontoinformasjon om aktører i Bidrassaker."))
@EnableConfigurationProperties()
public class AktoerregisterApplication {

    @Autowired
    private JobScheduler jobScheduler;

    public static void main(String[] args) {
        SpringApplication.run(AktoerregisterApplication.class, args);
    }

/*    @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }*/

    @PostConstruct
    public void startJobs() {
        jobScheduler.enqueue(TPSConsumerJob::execute);
    }
}
