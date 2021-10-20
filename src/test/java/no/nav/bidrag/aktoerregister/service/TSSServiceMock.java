package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;

public class TSSServiceMock implements TSSService {

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) {
    AktoerDTO aktoerDTO = new AktoerDTO();
    aktoerDTO.setAktoerId(aktoerId);

    AdresseDTO adresseDTO = new AdresseDTO();
    adresseDTO.setAdresselinje1("Testgate 1");

    aktoerDTO.setAdresse(adresseDTO);
    return aktoerDTO;
  }
}
