package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AdresseDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.rtv.namespacetss.TssSamhandlerData;

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

  @Override
  public TssSamhandlerData hentRawAktoer(AktoerIdDTO aktoerIdDTO) throws MQServiceException {
    return null;
  }
}
