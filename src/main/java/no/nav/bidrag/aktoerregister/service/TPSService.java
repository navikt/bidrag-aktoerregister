package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.rtv.namespacetps.TpsPersonData;

public interface TPSService {

  AktoerDTO hentAktoer(AktoerIdDTO aktoerId)
      throws MQServiceException, AktoerNotFoundException, TPSServiceException;

  TpsPersonData hentRawAktoer(AktoerIdDTO aktoerIdDTO) throws MQServiceException;
}
