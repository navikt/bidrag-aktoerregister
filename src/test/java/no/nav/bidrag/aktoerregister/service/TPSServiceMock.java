package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.rtv.namespacetps.TpsPersonData;

public class TPSServiceMock implements TPSService {

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) {
    AktoerDTO aktoerDTO = new AktoerDTO();
    aktoerDTO.setAktoerId(aktoerId);
    return aktoerDTO;
  }

  @Override
  public TpsPersonData hentRawAktoer(AktoerIdDTO aktoerIdDTO) throws MQServiceException {
    return null;
  }
}
