package no.nav.bidrag.aktoerregister.persistence.repository;

import no.nav.bidrag.aktoerregister.persistence.entities.Aktør;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AktørJpaRepository extends JpaRepository<Aktør, String> {

  Page<Aktør> findAllByAktørType(String aktørType, Pageable pageable);

  Aktør findByAktørIdent(String aktørIdent);

  void deleteAktørByAktørIdent(String aktørIdent);
}
