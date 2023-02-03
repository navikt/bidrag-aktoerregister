package no.nav.bidrag.aktoerregister.service;

import java.util.List;
import no.nav.bidrag.aktoerregister.domene.HendelseDTO;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

public interface AktoerregisterService extends AktoerService {

  Aktoer hentAktoerFromDB(String aktoerId);

  List<HendelseDTO> hentHendelser(int sekvensunummer, int antallHendelser);

  void oppdaterAktoer(Aktoer aktoer);

  void oppdaterAktoerer(List<Aktoer> aktoerer);

  void slettAktoer(String aktoerIdent);
}
