package no.nav.bidrag.aktoerregister.batch;

import lombok.Getter;
import lombok.Setter;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

@Getter
@Setter
public class TSSAktoerProcessorResult {

  private Aktoer aktoer;

  private AktoerStatus aktoerStatus;

  public TSSAktoerProcessorResult(Aktoer aktoer, AktoerStatus aktoerStatus) {
    this.aktoer = aktoer;
    this.aktoerStatus = aktoerStatus;
  }
}
