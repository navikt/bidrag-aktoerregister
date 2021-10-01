package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.rtv.namespacetss.TssSamhandlerData;

public class TSSServiceMock implements TSSService {

  @Override
  public AktoerDTO hentAktoer(AktoerIdDTO aktoerId) throws AktoerNotFoundException, TSSServiceException, MQServiceException {
    AktoerDTO aktoerDTO = new AktoerDTO();
    aktoerDTO.setAktoerId(aktoerId);
    return aktoerDTO;
  }

  @Override
  public TssSamhandlerData hentSamhandler(AktoerIdDTO aktoerId) throws MQServiceException {
    return null;
  }
}
