package no.nav.bidrag.aktoerregister.persistence.repository;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

public interface AktoerRepository {
  Aktoer insertOrUpdateAktoer(Aktoer aktoer);

  List<Aktoer> insertOrUpdateAktoerer(List<Aktoer> aktoerList);

  Aktoer getAktoer(String aktoerIdent);

  void deleteAktoer(String aktoerId);
}
