package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.rtv.namespacetps.TpsPersonData;

public class TPSServiceMock implements TPSService {

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) throws MQServiceException, AktoerNotFoundException, TPSServiceException {
    AktoerDTO aktoerDTO = new AktoerDTO();
    aktoerDTO.setAktoerId(aktoerId);
    return aktoerDTO;
  }

  @Override
  public TpsPersonData hentTpsPersonData(AktoerIdDTO aktoerId) throws MQServiceException {
    return null;
  }
}
