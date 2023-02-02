package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktoer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AktoerJpaRepository extends JpaRepository<Aktoer, String> {

  Page<Aktoer> findAllByAktoerType(String aktoerType, Pageable pageable);

  Aktoer findByAktoerIdent(String aktoerIdent);

  void deleteAktoerByAktoerIdent(String aktoerIdent);
}
