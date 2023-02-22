package no.nav.bidrag.aktoerregister.batch;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.service.AktoerregisterService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TSSAktoerWriter implements ItemWriter<TSSAktoerProcessorResult> {

  private final AktoerregisterService aktoerregisterService;

  @Autowired
  public TSSAktoerWriter(AktoerregisterService aktoerregisterService) {
    this.aktoerregisterService = aktoerregisterService;
  }

  @Override
  public void write(List<? extends TSSAktoerProcessorResult> list) {
    List<Aktoer> updatedAktoerList =
        list.stream()
            .filter(
                tssAktoerProcessorResult ->
                    tssAktoerProcessorResult.getAktoerStatus().equals(AktoerStatus.UPDATED))
            .map(TSSAktoerProcessorResult::getAktoer)
            .toList();
    if (!updatedAktoerList.isEmpty()) {
      aktoerregisterService.oppdaterAktoerer(updatedAktoerList);
    }
  }
}
