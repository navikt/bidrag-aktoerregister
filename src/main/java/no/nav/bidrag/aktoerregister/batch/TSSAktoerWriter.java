package no.nav.bidrag.aktoerregister.batch;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.repository.AktoerRepository;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TSSAktoerWriter implements ItemWriter<TSSAktoerProcessorResult> {

  @Autowired
  private AktoerRepository aktoerRepository;

  @Autowired
  private AktoerregisterService aktoerregisterService;

  @Override
  public void write(List<? extends TSSAktoerProcessorResult> list) {
    for (TSSAktoerProcessorResult tssAktoerProcessorResult : list) {
      Aktoer updatedAktoer = tssAktoerProcessorResult.getAktoer();
      if (tssAktoerProcessorResult.getAktoerStatus() == AktoerStatus.UPDATED) {
        aktoerregisterService.oppdaterAktoer(updatedAktoer);
      }
      //TODO: We might need to delete from db if id not found in TSS.
    }
  }
}
