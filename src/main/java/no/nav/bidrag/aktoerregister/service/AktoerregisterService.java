package no.nav.bidrag.aktoerregister.service;

import java.util.List;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;

public interface AktoerregisterService {

  AktoerDTO hentAktoer(AktoerIdDTO aktoerId) throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException;

  AktoerDTO hentAktoerFromDB(String aktoerId);

  List<HendelseDTO> hentHendelser(int sekvensunummer, int antallHendelser);

  void oppdaterAktoer(AktoerDTO aktoer);

  void slettAktoer(String aktoerId);
}
