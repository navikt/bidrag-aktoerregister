package no.nav.bidrag.aktoerregister.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.jms.ConnectionFactory;
import no.nav.bidrag.aktoerregister.AktoerregisterApplication;
import no.nav.bidrag.aktoerregister.domene.enumer.IdenttypeDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;
import no.nav.bidrag.aktoerregister.service.TSSServiceImpl;
import no.nav.bidrag.aktoerregister.service.mq.TPSConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@SpringBatchTest
@PropertySource("classpath:persistence.properties")
@EnableAutoConfiguration
@ContextConfiguration(classes = {AktoerregisterApplication.class})
public class TSSBatchTest {

  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired private JobRepositoryTestUtils jobRepositoryTestUtils;
  @Autowired private AktoerRepository aktoerRepository;
  @MockBean private TSSServiceImpl tssService;
  @MockBean private TPSConsumer tpsConsumer;
  @MockBean private ConnectionFactory connectionFactory;

  @AfterEach
  public void cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions();
  }

  @Test
  public void skalKjoreTssBatchVellykket() throws Exception {
    opprettAktoererOgMocks();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString("time", dateFormat.format(Calendar.getInstance().getTime()))
            .toJobParameters();

    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    assertEquals(TSSBatchConfig.TSS_AKTOER_UPDATES_JOB, actualJobInstance.getJobName());
    assertEquals("COMPLETED", actualJobExitStatus.getExitCode());

    StepExecution updateTSSAktoererStep =
        jobExecution.getStepExecutions().stream()
            .filter(
                stepExecution ->
                    stepExecution.getStepName().equals(TSSBatchConfig.TSS_UPDATE_AKTOERER_STEP))
            .findFirst()
            .orElse(null);
    assertNotNull(updateTSSAktoererStep);
    assertEquals(100, updateTSSAktoererStep.getFilterCount());
    assertEquals(
        50, updateTSSAktoererStep.getExecutionContext().getLong(TSSBatchConfig.NR_UPDATED));
  }

  private void opprettAktoererOgMocks() {
    List<Aktoer> aktoerList = new ArrayList<>();
    for (int i = 0; i < 150; i++) {
      Aktoer aktoer =
          Aktoer.builder()
              .aktoerIdent(UUID.randomUUID().toString())
              .aktoerType(IdenttypeDTO.AKTOERNUMMER.name())
              .build();
      aktoerList.add(aktoer);
    }
    aktoerRepository.opprettEllerOppdaterAktoerer(aktoerList);

    for (int i = 0; i < aktoerList.size(); i++) {
      Aktoer aktoer = aktoerList.get(i);
      if (i < 50) {
        aktoer.setNorskKontonr("12345678910");
      }
      Mockito.when(tssService.hentAktoer(aktoer.getAktoerIdent())).thenReturn(aktoer);
    }
  }
}
