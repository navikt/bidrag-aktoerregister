package no.nav.bidrag.aktoerregister;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Bidrag Aktørregister", version = "0.2", description = "Inneholder adresse- og kontoinformasjon om aktører i Bidrassaker."))
@EnableConfigurationProperties()
public class AktoerregisterApplicationTest {

  public static void main(String[] args) {
    SpringApplication.run(AktoerregisterApplicationTest.class, args);
  }

  @Bean
  public StorageProvider storageProvider(JobMapper jobMapper) {
      InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
      storageProvider.setJobMapper(jobMapper);
      return storageProvider;
  }

}
