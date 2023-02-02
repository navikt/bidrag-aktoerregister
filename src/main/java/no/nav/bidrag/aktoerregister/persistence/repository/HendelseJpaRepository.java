package no.nav.bidrag.aktoerregister.persistence.repository;

import java.util.List;
import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HendelseJpaRepository extends JpaRepository<Hendelse, Integer> {

  /**
   * Henter en liste over med siste Hendelse for hver Aktoer hvor sekvensnummer til Hendelse
   * er større enn det spesifiserte sekvensnummer og begrenset av det gitte antallet omgjort til en pageable.
   */
  @Query(value = "SELECT new Hendelse(max(h.sekvensnummer), h.aktoer) FROM Hendelse h WHERE h.sekvensnummer >= :sekvensnummer GROUP BY h.aktoer.id ORDER BY max(h.sekvensnummer)")
  List<Hendelse> hentHendelserMedUnikAktoer(int sekvensnummer, Pageable pageable);
}
