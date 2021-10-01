package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;

public interface AktoerRepository {
  Aktoer insertOrUpdateAktoer(Aktoer aktoer);

  Aktoer getAktoer(String aktoerId);

  void deleteAktoer(String aktoerId);
}
