package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface HendelseJpaRepository : JpaRepository<Hendelse, Int> {

    /**
     * Henter en liste over med siste Hendelse for hver Aktoer hvor sekvensnummer til Hendelse
     * er større enn det spesifiserte sekvensnummer og begrenset av det gitte antallet omgjort til en pageable.
     */
    @Query(value = "SELECT new Hendelse(max(h.sekvensnummer), h.aktør) FROM Hendelse h WHERE h.sekvensnummer >= :sekvensnummer GROUP BY h.aktør.id ORDER BY max(h.sekvensnummer)")
    fun hentHendelserMedUnikAktoer(@Param("sekvensnummer") sekvensnummer: Int, pageable: Pageable): List<Hendelse>
}
