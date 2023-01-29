package no.nav.bidrag.aktoerregister.batch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class TSSBatchSchedulerConfig {

  private final JobLauncher jobLauncher;

  private final Job job;

  @Autowired
  public TSSBatchSchedulerConfig(JobLauncher jobLauncher, Job job) {
    this.jobLauncher = jobLauncher;
    this.job = job;
  }

  @Scheduled(cron = "0 0/30 * * * *")
  @SchedulerLock(
      name = TSSBatchConfig.TSS_AKTOER_UPDATES_JOB,
      lockAtMostFor = "30s",
      lockAtLeastFor = "30s")
  public void scheduleTSSBatch()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
          JobParametersInvalidException, JobRestartException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString("time", dateFormat.format(Calendar.getInstance().getTime()))
            .toJobParameters();
    jobLauncher.run(job, jobParameters);
  }
}
