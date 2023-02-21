package no.nav.bidrag.aktoerregister.batch;

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
  private final Logger LOGGER = LoggerFactory.getLogger(TSSAktoerProcessor.class);

  @Autowired
  public TSSAktoerProcessor(@Qualifier("TSSServiceImpl") AktoerService tssService) {
    this.tssService = tssService;
  }

  @Override
  public TSSAktoerProcessorResult process(Aktoer aktoer) {
    Aktoer tssAktoer = tssService.hentAktoer(aktoer.getAktoerIdent());
    if (!tssAktoer.equals(aktoer)) {
      LOGGER.debug("Oppdaterer aktør med ident: {}", aktoer.getAktoerIdent());
      aktoer.oppdaterAlleFelter(tssAktoer);
      return new TSSAktoerProcessorResult(aktoer, AktoerStatus.UPDATED);
    }
    return null;
  }
}
