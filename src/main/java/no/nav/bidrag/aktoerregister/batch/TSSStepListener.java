package no.nav.bidrag.aktoerregister.batch;

import java.util.List;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;

public class TSSStepListener {

  private ExecutionContext executionContext;

  @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
    this.executionContext = stepExecution.getExecutionContext();
  }

  @AfterWrite
  public void afterWrite(List<TSSAktoerProcessorResult> items) {
    long nrOfUpdatedAktoerer =
        items.stream()
            .filter(
                tssAktoerProcessorResult ->
                    tssAktoerProcessorResult.getAktoerStatus().equals(AktoerStatus.UPDATED))
            .count();
    incrementExecutionContextValue(TSSBatchConfig.NR_UPDATED, nrOfUpdatedAktoerer);
    incrementExecutionContextValue(TSSBatchConfig.NR_TOTAL, items.size());
  }

  private void incrementExecutionContextValue(String key, long amount) {
    if (!this.executionContext.containsKey(key)) {
      this.executionContext.putLong(key, 0L);
    }
    long totalAmount = this.executionContext.getLong(key);
    totalAmount += amount;
    this.executionContext.putLong(key, totalAmount);
  }
}
