package no.nav.bidrag.aktoerregister.persistence.repository;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HendelseJpaRepository extends JpaRepository<Hendelse, Integer> {

  /**
   * Retrieves a list of the latest Hendelse for each Aktoer where the sekvensnummer of the Hendelse
   * is greater than the specified sekvensnummer and limited by the given pageable.
   *
   * @param sekvensnummer
   * @param pageable
   * @return
   */
  @Query(
      value =
          "SELECT new Hendelse(max(h.sekvensnummer), h.aktoer) FROM Hendelse h WHERE h.sekvensnummer >= :sekvensnummer GROUP BY h.aktoer.aktoerId ORDER BY max(h.sekvensnummer)")
  List<Hendelse> getHendelserWithUniqueAktoerPageable(int sekvensnummer, Pageable pageable);
}
