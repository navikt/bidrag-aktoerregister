package no.nav.bidrag.aktoerregister.persistence.repository;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

public interface AktoerRepository {
  Aktoer opprettEllerOppdaterAktoer(Aktoer aktoer);

  List<Aktoer> opprettEllerOppdaterAktoerer(List<Aktoer> aktoerList);

  Aktoer getAktoer(String aktoerIdent);

  void deleteAktoer(String aktoerId);
}
