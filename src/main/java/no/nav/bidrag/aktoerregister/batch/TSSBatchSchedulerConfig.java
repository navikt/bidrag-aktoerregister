package no.nav.bidrag.aktoerregister.batch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Logger logger = LoggerFactory.getLogger(TSSBatchSchedulerConfig.class);

  @Autowired
  JobLauncher jobLauncher;

  @Autowired
  Job job;

  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

  @Scheduled(cron = "0 0/1 * * * *")
  @SchedulerLock(name = "TSSAktoerUpdatesJob", lockAtMostFor = "30s", lockAtLeastFor = "30s")
  public void scheduleTSSBatch()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    logger.info("Starting TSS batch job");
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("time", format.format(Calendar.getInstance().getTime())).toJobParameters();
    jobLauncher.run(job, jobParameters);
    logger.info("TSS batch job executed successfully");
  }
}
