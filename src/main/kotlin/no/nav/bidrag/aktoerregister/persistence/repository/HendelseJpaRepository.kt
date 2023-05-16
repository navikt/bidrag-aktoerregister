package no.nav.bidrag.aktoerregister.persistence.repository

import no.nav.bidrag.aktoerregister.persistence.entities.Hendelse
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface HendelseJpaRepository : JpaRepository<Hendelse, Int> {

    fun getAllBySekvensnummerGreaterThan(sekvensnummer: Int, pageable: Pageable): List<Hendelse>
}
