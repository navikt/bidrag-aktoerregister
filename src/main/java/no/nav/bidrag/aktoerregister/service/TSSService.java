package no.nav.bidrag.aktoerregister.service;

import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;

public interface TSSService {

  AktoerDTO hentAktoer(AktoerIdDTO aktoerId)
      throws AktoerNotFoundException, TSSServiceException, MQServiceException;
}
