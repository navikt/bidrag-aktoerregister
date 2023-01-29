package no.nav.bidrag.aktoerregister.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.item.ExecutionContext;

public class TSSJobListener {

  private final Logger logger = LoggerFactory.getLogger(TSSJobListener.class);

  @AfterJob
  public void afterJob(JobExecution jobExecution) {
    StepExecution updateTSSAktoererStep =
        jobExecution.getStepExecutions().stream()
            .filter(
                stepExecution ->
                    stepExecution.getStepName().equals(TSSBatchConfig.TSS_UPDATE_AKTOERER_STEP))
            .findFirst()
            .orElse(null);
    if (updateTSSAktoererStep != null) {
      long nrTotalAktoerer =
          getLongFromExecutionContext(
              TSSBatchConfig.NR_TOTAL, updateTSSAktoererStep.getExecutionContext());
      long nrOfUpdatedAktoerer =
          getLongFromExecutionContext(
              TSSBatchConfig.NR_UPDATED, updateTSSAktoererStep.getExecutionContext());
      logger.info(
          "Av totalt {} aktoerer var {} blitt endret siden forrige kjøring. {} aktoerer ble dermed oppdatert i databasen.",
          nrTotalAktoerer,
          nrOfUpdatedAktoerer,
          nrOfUpdatedAktoerer);
    }
  }

  private long getLongFromExecutionContext(String key, ExecutionContext executionContext) {
    try {
      return executionContext.getLong(key);
    } catch (ClassCastException exception) {
      // Value is not Long or is Null
      return 0;
    }
  }
}
