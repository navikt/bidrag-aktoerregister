package no.nav.bidrag.aktoerregister.persistence.repository;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;

public interface HendelseRepository {

  List<Hendelse> hentHendelser(int fraSekvensnummer, int antallHendelser);

  void opprettHendelser(List<Aktoer> updatedAktoerer);
}
