package no.nav.bidrag.aktoerregister.batch;

import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.service.AktoerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TSSAktoerProcessor implements ItemProcessor<Aktoer, TSSAktoerProcessorResult> {

  private final AktoerService tssService;
  private final Logger logger = LoggerFactory.getLogger(TSSAktoerProcessor.class);

  @Autowired
  public TSSAktoerProcessor(@Qualifier("TSSServiceImpl") AktoerService tssService) {
    this.tssService = tssService;
  }

  @Override
  public TSSAktoerProcessorResult process(Aktoer aktoer) {
    try {
      Aktoer tssAktoer = tssService.hentAktoer(aktoer.getAktoerIdent());
      if (!tssAktoer.equals(aktoer)) {
        aktoer.oppdaterAlleFelter(tssAktoer);
        return new TSSAktoerProcessorResult(aktoer, AktoerStatus.UPDATED);
      }
    } catch (MQServiceException | TSSServiceException | AktoerNotFoundException e) {
      logger.error(e.getMessage(), e);
      throw e;
    }
    return null;
  }
}
