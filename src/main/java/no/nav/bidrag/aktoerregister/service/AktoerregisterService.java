package no.nav.bidrag.aktoerregister.service;

import java.util.List;
import no.nav.bidrag.aktoerregister.domene.AktoerDTO;
import no.nav.bidrag.aktoerregister.domene.AktoerIdDTO;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.exception.AktoerNotFoundException;
import no.nav.bidrag.aktoerregister.exception.MQServiceException;
import no.nav.bidrag.aktoerregister.exception.TPSServiceException;
import no.nav.bidrag.aktoerregister.exception.TSSServiceException;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

public interface AktoerregisterService {

  AktoerDTO hentAktoer(AktoerIdDTO aktoerId) throws MQServiceException, TSSServiceException, AktoerNotFoundException, TPSServiceException;

  Aktoer hentAktoerFromDB(String aktoerId);

  List<HendelseDTO> hentHendelser(int sekvensunummer, int antallHendelser);

  void oppdaterAktoer(Aktoer aktoer);

  void oppdaterAktoerer(List<Aktoer> aktoerer);

  void slettAktoer(String aktoerId);
}
