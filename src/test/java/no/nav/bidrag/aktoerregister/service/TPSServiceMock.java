package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;

public class TPSServiceMock implements AktoerService {

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) {
    AktoerDTO aktoerDTO = new AktoerDTO();
    aktoerDTO.setAktoerId(aktoerId);
    return aktoerDTO;
  }
}
