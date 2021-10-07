 package no.nav.bidrag.aktoerregister.batch;

import java.util.Set;
import lombok.SneakyThrows;
import no.nav.bidrag.aktoerregister.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TSSBatchJobExecutionListener implements JobExecutionListener {

  private Logger logger = LoggerFactory.getLogger(TSSBatchJobExecutionListener.class);

  private JobExplorer jobExplorer;

  private JobOperator jobOperator;

  public TSSBatchJobExecutionListener(JobExplorer jobExplorer, JobOperator jobOperator) {
    this.jobExplorer = jobExplorer;
    this.jobOperator = jobOperator;
  }

  @SneakyThrows
  @Override
  public void beforeJob(JobExecution jobExecution) {
    Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(jobExecution.getJobInstance().getJobName());
    for (JobExecution runningJobExecution : runningJobExecutions) {
      logger.info("Kjørende batch jobb {} {} {}: ", runningJobExecution.getJobInstance().getJobName(), runningJobExecution.getStatus(), runningJobExecution.getStartTime());
    }
    logger.info("Attempting to start job: {} with id: {} and parameters: {}", jobExecution.getJobInstance().getJobName(), jobExecution.getJobInstance().getId(),
        JsonUtil.objectToJsonString(jobExecution.getJobParameters()));
    if (runningJobExecutions.size() > 1) {
      for(JobExecution runningJobExecution : runningJobExecutions) {
        if(!runningJobExecution.getJobId().equals(jobExecution.getJobId())) {
          jobExecution.setExitStatus(ExitStatus.STOPPED);
          logger.info("Stopping job execution of {}, the job is already running on another pod.", jobExecution.getJobInstance().getJobName());
        }
      }
    }
    logger.info("Job execution of {} passed beforeJob checks and will start execution", jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    logger.info("Job execution of {} finished", jobExecution.getJobInstance().getJobName());
  }
}
