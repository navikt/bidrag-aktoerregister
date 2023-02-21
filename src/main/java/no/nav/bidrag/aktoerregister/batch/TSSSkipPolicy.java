package no.nav.bidrag.aktoerregister.batch;

import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class TSSSkipPolicy implements SkipPolicy {

  private final Logger LOGGER = LoggerFactory.getLogger(TSSSkipPolicy.class);
  private final int skipLimit;

  public TSSSkipPolicy(int skipLimit) {
    this.skipLimit = skipLimit;
  }

  @Override
  public boolean shouldSkip(@NotNull Throwable throwable, int skipCount)
      throws SkipLimitExceededException {

    if (skipCount > skipLimit) {
      LOGGER.error(
          "TSS batch har nådd maks antall skips({}) i en kjøring. Kjøringen avsluttes. \nSiste feil var: {}",
          skipLimit,
          throwable.getMessage());
      return false;
    }

    if (throwable instanceof AktoerNotFoundException || throwable instanceof MQServiceException) {
      LOGGER.error(throwable.getMessage());
      return true;
    }

    return false;
  }
}
